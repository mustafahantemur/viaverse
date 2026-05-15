package app.viaverse.identity.account.infrastructure.persistence.adapter;

import app.viaverse.identity.account.application.port.out.AccountRepository;
import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.identity.account.infrastructure.persistence.mapper.AccountJpaMapper;
import app.viaverse.identity.account.infrastructure.persistence.repository.IdentityAccountJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class AccountJpaAdapter implements AccountRepository {

    private final IdentityAccountJpaRepository repository;
    private final AccountJpaMapper mapper;

    public AccountJpaAdapter(IdentityAccountJpaRepository repository, AccountJpaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Account save(Account account) {
        return mapper.toDomain(repository.save(mapper.toEntity(account)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }
}
