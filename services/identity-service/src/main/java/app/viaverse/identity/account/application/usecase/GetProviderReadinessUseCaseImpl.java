package app.viaverse.identity.account.application.usecase;

import app.viaverse.identity.account.application.port.in.GetProviderReadinessUseCase;
import app.viaverse.identity.account.application.port.out.AccountRepository;
import app.viaverse.identity.account.domain.AccountStatusEnum;
import app.viaverse.identity.auth.application.port.out.IdentifierRepository;
import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetProviderReadinessUseCaseImpl implements GetProviderReadinessUseCase {

    private final AccountRepository accountRepository;
    private final IdentifierRepository identifierRepository;

    public GetProviderReadinessUseCaseImpl(
            AccountRepository accountRepository,
            IdentifierRepository identifierRepository
    ) {
        this.accountRepository = accountRepository;
        this.identifierRepository = identifierRepository;
    }

    @Override
    @ObservedAction("identity.internal.provider-readiness")
    public Result execute(UUID accountId) {
        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        boolean hasVerifiedIdentifier = identifierRepository.findByAccountId(accountId).stream()
                .anyMatch(identifier -> identifier.identifierType() == IdentifierTypeEnum.EMAIL
                        || identifier.identifierType() == IdentifierTypeEnum.PHONE);
        return new Result(accountId, account.getStatus() == AccountStatusEnum.ACTIVE, hasVerifiedIdentifier);
    }
}
