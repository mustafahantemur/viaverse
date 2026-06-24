package app.viaverse.media.asset.application.usecase;

import app.viaverse.media.asset.application.port.in.CompleteUploadUseCase;
import app.viaverse.media.asset.application.port.out.MediaAssetRepository;
import app.viaverse.media.asset.application.port.out.MediaEventPublisher;
import app.viaverse.media.asset.application.port.out.ObjectStorageGateway;
import app.viaverse.media.asset.domain.model.MediaAsset;
import app.viaverse.shared.kernel.error.ConflictException;
import app.viaverse.shared.kernel.error.ForbiddenException;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompleteUploadUseCaseImpl implements CompleteUploadUseCase {
    private final MediaAssetRepository repository;
    private final ObjectStorageGateway objectStorageGateway;
    private final MediaEventPublisher eventPublisher;
    private final Clock clock;

    public CompleteUploadUseCaseImpl(
            MediaAssetRepository repository,
            ObjectStorageGateway objectStorageGateway,
            MediaEventPublisher eventPublisher,
            Clock clock
    ) {
        this.repository = repository;
        this.objectStorageGateway = objectStorageGateway;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Override
    @ObservedAction("media.upload.complete")
    @Transactional
    public MediaAsset execute(Command command) {
        MediaAsset current = repository.findById(command.assetId())
                .orElseThrow(() -> new NotFoundException("Media asset not found"));
        if (!current.getOwnerAccountId().equals(command.ownerAccountId())) {
            throw new ForbiddenException("Only the asset owner can complete an upload");
        }
        try {
            ObjectStorageGateway.UploadedObject uploadedObject = objectStorageGateway.inspectUploadedObject(current);
            MediaAsset saved = repository.save(current.markReady(
                    uploadedObject.byteSize(),
                    command.checksumSha256(),
                    clock.instant()
            ));
            eventPublisher.publishAssetReady(saved);
            return saved;
        } catch (IllegalStateException exception) {
            throw new ConflictException(exception.getMessage());
        }
    }
}
