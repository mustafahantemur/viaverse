package app.viaverse.content.post.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.content.post.application.port.out.ContentInteractionRepository;
import app.viaverse.content.post.domain.enums.ContentSignalTypeEnum;
import app.viaverse.content.post.domain.model.ContentInteraction;
import app.viaverse.content.post.infrastructure.adapter.out.persistence.mapper.ContentJpaMapper;
import app.viaverse.content.post.infrastructure.adapter.out.persistence.repository.ContentInteractionJpaRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ContentInteractionJpaAdapter implements ContentInteractionRepository {
    private static final List<ContentSignalTypeEnum> SUPPRESSION_SIGNALS =
            List.of(ContentSignalTypeEnum.HIDE, ContentSignalTypeEnum.REPORT);
    private final ContentInteractionJpaRepository repository;
    private final ContentJpaMapper mapper;

    public ContentInteractionJpaAdapter(ContentInteractionJpaRepository repository, ContentJpaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ContentInteraction save(ContentInteraction interaction) {
        return mapper.toDomain(repository.save(mapper.toEntity(interaction)));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<UUID> findSuppressedPostIds(UUID viewerAccountId) {
        return repository.findSuppressedPostIds(viewerAccountId, SUPPRESSION_SIGNALS);
    }
}
