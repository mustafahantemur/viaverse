package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.ListCurrentBlocksUseCase;
import app.viaverse.profile.profile.application.port.out.ProfileBlockRepository;
import app.viaverse.profile.profile.domain.model.ProfileBlock;
import app.viaverse.web.logging.ObservedAction;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ListCurrentBlocksUseCaseImpl implements ListCurrentBlocksUseCase {

    private final ProfileBlockRepository repository;

    public ListCurrentBlocksUseCaseImpl(ProfileBlockRepository repository) {
        this.repository = repository;
    }

    @Override
    @ObservedAction("profile.blocks.list")
    public List<ProfileBlock> execute(UUID blockerAccountId) {
        return repository.findAllByBlockerAccountId(blockerAccountId);
    }
}
