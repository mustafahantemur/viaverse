package app.viaverse.identity.auth.infrastructure.adapter.out.persistence.mapper;

import app.viaverse.identity.auth.domain.model.AdminInvitation;
import app.viaverse.identity.auth.infrastructure.adapter.out.persistence.entity.AdminInvitationJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminInvitationJpaMapper {
    AdminInvitation toDomain(AdminInvitationJpaEntity entity);

    AdminInvitationJpaEntity toEntity(AdminInvitation invitation);
}
