package app.viaverse.identity.account.application.usecase;

import app.viaverse.identity.account.application.port.in.GetInternalAccountUseCase;
import app.viaverse.identity.account.application.port.out.AccountRepository;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetInternalAccountUseCaseImpl implements GetInternalAccountUseCase {

    private final AccountRepository accountRepository;

    public GetInternalAccountUseCaseImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @ObservedAction("identity.internal.account")
    public Result execute(UUID accountId) {
        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        return new Result(
                account.getId(),
                account.getDisplayName(),
                account.getFirstName(),
                account.getLastName(),
                account.getCreatedAt()
        );
    }
}
