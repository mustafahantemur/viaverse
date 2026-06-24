package app.viaverse.trustgamification.trust.application.usecase;

import app.viaverse.contracts.profile.profile.ProfileEventTypes;
import app.viaverse.trustgamification.trust.application.port.in.BootstrapTrustFromProfileCreatedUseCase;
import app.viaverse.trustgamification.trust.application.port.out.ConsumedEventRepository;
import app.viaverse.trustgamification.trust.application.port.out.TrustScoreEventPublisher;
import app.viaverse.trustgamification.trust.application.port.out.TrustStateRepository;
import app.viaverse.trustgamification.trust.domain.model.TrustState;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BootstrapTrustFromProfileCreatedUseCaseImpl implements BootstrapTrustFromProfileCreatedUseCase {

    private final TrustStateRepository trustStateRepository;
    private final ConsumedEventRepository consumedEventRepository;
    private final TrustScoreEventPublisher trustScoreEventPublisher;
    private final Clock clock;

    public BootstrapTrustFromProfileCreatedUseCaseImpl(
            TrustStateRepository trustStateRepository,
            ConsumedEventRepository consumedEventRepository,
            TrustScoreEventPublisher trustScoreEventPublisher,
            Clock clock
    ) {
        this.trustStateRepository = trustStateRepository;
        this.consumedEventRepository = consumedEventRepository;
        this.trustScoreEventPublisher = trustScoreEventPublisher;
        this.clock = clock;
    }

    @Override
    @ObservedAction("trust.bootstrap")
    @Transactional
    public Result bootstrap(Command command) {
        Objects.requireNonNull(command, "command");
        if (consumedEventRepository.existsByEventId(command.eventId())) {
            return new Result(command.accountId(), false);
        }
        if (trustStateRepository.findByAccountId(command.accountId()).isPresent()) {
            recordConsumed(command.eventId());
            return new Result(command.accountId(), false);
        }

        TrustState saved = trustStateRepository.save(TrustState.baseline(command.accountId(), command.occurredAt()));
        trustScoreEventPublisher.publishUpdated(saved);
        recordConsumed(command.eventId());
        return new Result(saved.getAccountId(), true);
    }

    private void recordConsumed(java.util.UUID eventId) {
        consumedEventRepository.record(
                eventId,
                ProfileEventTypes.PROFILE_CREATED_V1,
                Instant.now(clock)
        );
    }
}
