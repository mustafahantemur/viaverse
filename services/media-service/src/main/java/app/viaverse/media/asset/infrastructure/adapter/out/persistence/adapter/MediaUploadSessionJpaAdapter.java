package app.viaverse.media.asset.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.media.asset.application.port.out.MediaUploadSessionRepository;
import app.viaverse.media.asset.domain.model.MediaUploadSession;
import app.viaverse.media.asset.infrastructure.adapter.out.persistence.mapper.MediaJpaMapper;
import app.viaverse.media.asset.infrastructure.adapter.out.persistence.repository.MediaUploadSessionJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class MediaUploadSessionJpaAdapter implements MediaUploadSessionRepository {
    private final MediaUploadSessionJpaRepository repository;
    private final MediaJpaMapper mapper;

    public MediaUploadSessionJpaAdapter(MediaUploadSessionJpaRepository repository, MediaJpaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public MediaUploadSession save(MediaUploadSession session) {
        return mapper.toDomain(repository.save(mapper.toEntity(session)));
    }
}
