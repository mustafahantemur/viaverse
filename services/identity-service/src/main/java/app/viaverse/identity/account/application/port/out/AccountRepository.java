package app.viaverse.identity.account.application.port.out;

import app.viaverse.identity.account.domain.model.Account;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(UUID id);
}
