package app.viaverse.identity.account.application.usecase;

import app.viaverse.contracts.profile.profile.ProfileEventTypes;
import app.viaverse.identity.account.application.port.in.MirrorProfileUpdatedUseCase;
import app.viaverse.identity.account.application.port.out.AccountRepository;
import app.viaverse.identity.account.application.port.out.ConsumedEventRepository;
import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MirrorProfileUpdatedUseCaseImpl implements MirrorProfileUpdatedUseCase {

    private final AccountRepository accountRepository;
    private final ConsumedEventRepository consumedEventRepository;
    private final Clock clock;

    public MirrorProfileUpdatedUseCaseImpl(
            AccountRepository accountRepository,
            ConsumedEventRepository consumedEventRepository,
            Clock clock
    ) {
        this.accountRepository = accountRepository;
        this.consumedEventRepository = consumedEventRepository;
        this.clock = clock;
    }

    @Override
    @ObservedAction("account.profile-mirror.update")
    @Transactional
    public Result mirror(Command command) {
        Objects.requireNonNull(command, "command");
        if (consumedEventRepository.existsByEventId(command.eventId())) {
            return new Result(command.accountId(), false);
        }

        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found"));
        account.mirrorDisplayFields(
                command.displayName(),
                command.firstName(),
                command.lastName(),
                command.occurredAt()
        );
        accountRepository.save(account);
        consumedEventRepository.record(
                command.eventId(),
                ProfileEventTypes.PROFILE_UPDATED_V1,
                Instant.now(clock)
        );
        return new Result(command.accountId(), true);
    }
}
