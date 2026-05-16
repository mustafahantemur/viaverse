package app.viaverse.identity.auth.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.identity.auth.application.port.out.AdminInvitationRepository;
import app.viaverse.identity.auth.domain.model.AdminInvitation;
import app.viaverse.identity.auth.infrastructure.adapter.out.persistence.mapper.AdminInvitationJpaMapper;
import app.viaverse.identity.auth.infrastructure.adapter.out.persistence.repository.AdminInvitationJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class AdminInvitationJpaAdapter implements AdminInvitationRepository {
    private final AdminInvitationJpaRepository repository;
    private final AdminInvitationJpaMapper mapper;

    public AdminInvitationJpaAdapter(
            AdminInvitationJpaRepository repository,
            AdminInvitationJpaMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public AdminInvitation save(AdminInvitation invitation) {
        return mapper.toDomain(repository.save(mapper.toEntity(invitation)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AdminInvitation> findByTokenHash(String tokenHash) {
        return repository.findByTokenHash(tokenHash).map(mapper::toDomain);
    }
}
