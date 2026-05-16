package app.viaverse.identity.account.infrastructure.adapter.in.web.mapper;

import app.viaverse.identity.account.domain.AccountView;
import app.viaverse.identity.account.domain.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Maps the {@link Account} domain model to the transport-facing
 * {@link AccountView} record.
 */
@Mapper(componentModel = "spring")
public interface AccountDtoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "displayName", source = "displayName")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "profileCompleted", source = "profileCompleted")
    @Mapping(target = "createdAt", source = "createdAt")
    AccountView toView(Account account);
}
