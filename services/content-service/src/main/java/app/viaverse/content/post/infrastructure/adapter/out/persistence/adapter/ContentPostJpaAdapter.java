package app.viaverse.content.post.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.content.post.application.port.out.ContentPostRepository;
import app.viaverse.content.post.domain.enums.ContentPostStatusEnum;
import app.viaverse.content.post.domain.model.ContentPost;
import app.viaverse.content.post.infrastructure.adapter.out.persistence.mapper.ContentJpaMapper;
import app.viaverse.content.post.infrastructure.adapter.out.persistence.repository.ContentPostJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ContentPostJpaAdapter implements ContentPostRepository {

    private final ContentPostJpaRepository repository;
    private final ContentJpaMapper mapper;

    public ContentPostJpaAdapter(ContentPostJpaRepository repository, ContentJpaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ContentPost save(ContentPost post) {
        return mapper.toDomain(repository.save(mapper.toEntity(post)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ContentPost> findById(UUID postId) {
        return repository.findById(postId).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentPost> findAllByAuthorAccountId(UUID authorAccountId) {
        return repository.findAllByAuthorAccountIdOrderByCreatedAtDesc(authorAccountId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentPost> findAllPublished(String city, String district) {
        if (city != null && !city.isBlank() && district != null && !district.isBlank()) {
            return repository.findAllByStatusAndCityAndDistrictOrderByPublishedAtDesc(
                            ContentPostStatusEnum.PUBLISHED,
                            city,
                            district
                    ).stream()
                    .map(mapper::toDomain)
                    .toList();
        }
        if (city != null && !city.isBlank()) {
            return repository.findAllByStatusAndCityOrderByPublishedAtDesc(
                            ContentPostStatusEnum.PUBLISHED,
                            city
                    ).stream()
                    .map(mapper::toDomain)
                    .toList();
        }
        return repository.findAllByStatusOrderByPublishedAtDesc(ContentPostStatusEnum.PUBLISHED).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
