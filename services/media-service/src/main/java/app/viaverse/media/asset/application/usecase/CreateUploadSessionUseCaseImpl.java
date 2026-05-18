package app.viaverse.media.asset.application.usecase;

import app.viaverse.media.asset.application.port.in.CreateUploadSessionUseCase;
import app.viaverse.media.asset.application.port.out.MediaAssetRepository;
import app.viaverse.media.asset.application.port.out.MediaUploadSessionRepository;
import app.viaverse.media.asset.application.port.out.ObjectStorageGateway;
import app.viaverse.media.asset.domain.model.MediaAsset;
import app.viaverse.media.asset.domain.model.MediaUploadSession;
import app.viaverse.media.config.MediaProperties;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.time.Duration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateUploadSessionUseCaseImpl implements CreateUploadSessionUseCase {
    private final MediaAssetRepository assetRepository;
    private final MediaUploadSessionRepository sessionRepository;
    private final ObjectStorageGateway objectStorageGateway;
    private final MediaProperties properties;
    private final Clock clock;

    public CreateUploadSessionUseCaseImpl(
            MediaAssetRepository assetRepository,
            MediaUploadSessionRepository sessionRepository,
            ObjectStorageGateway objectStorageGateway,
            MediaProperties properties,
            Clock clock
    ) {
        this.assetRepository = assetRepository;
        this.sessionRepository = sessionRepository;
        this.objectStorageGateway = objectStorageGateway;
        this.properties = properties;
        this.clock = clock;
    }

    @Override
    @ObservedAction("media.upload_session.create")
    @Transactional
    public Result execute(Command command) {
        var now = clock.instant();
        MediaAsset asset = assetRepository.save(MediaAsset.initiate(
                command.ownerAccountId(),
                command.assetKind(),
                command.contentType(),
                command.originalFileName(),
                now
        ));
        Duration ttl = Duration.ofMinutes(properties.getUploadSessionTtlMinutes());
        MediaUploadSession session = sessionRepository.save(MediaUploadSession.create(
                asset.getId(),
                now.plus(ttl),
                now
        ));
        ObjectStorageGateway.UploadTarget uploadTarget = objectStorageGateway.createPresignedUpload(asset, ttl);
        return new Result(asset, session, uploadTarget.url(), uploadTarget.requiredHeaders());
    }
}
