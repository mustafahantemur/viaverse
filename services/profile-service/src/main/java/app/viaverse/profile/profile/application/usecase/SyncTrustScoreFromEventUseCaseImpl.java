package app.viaverse.profile.profile.application.usecase;

import app.viaverse.contracts.trust.score.TrustEventTypes;
import app.viaverse.profile.profile.application.port.in.SyncTrustScoreFromEventUseCase;
import app.viaverse.profile.profile.application.port.out.ConsumedEventRepository;
import app.viaverse.profile.profile.application.port.out.ProfileTrustSnapshotRepository;
import app.viaverse.profile.profile.domain.enums.TrustBadgeEnum;
import app.viaverse.profile.profile.domain.enums.TrustLevelEnum;
import app.viaverse.profile.profile.domain.model.ProfileTrustSnapshot;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SyncTrustScoreFromEventUseCaseImpl implements SyncTrustScoreFromEventUseCase {

    private final ProfileTrustSnapshotRepository trustSnapshotRepository;
    private final ConsumedEventRepository consumedEventRepository;
    private final Clock clock;

    public SyncTrustScoreFromEventUseCaseImpl(
            ProfileTrustSnapshotRepository trustSnapshotRepository,
            ConsumedEventRepository consumedEventRepository,
            Clock clock
    ) {
        this.trustSnapshotRepository = trustSnapshotRepository;
        this.consumedEventRepository = consumedEventRepository;
        this.clock = clock;
    }

    @Override
    @ObservedAction("profile.trust.sync")
    @Transactional
    public Result sync(Command command) {
        Objects.requireNonNull(command, "command");
        if (consumedEventRepository.existsByEventId(command.eventId())) {
            return new Result(command.accountId(), false);
        }

        TrustLevelEnum level = TrustLevelEnum.valueOf(command.level());
        TrustBadgeEnum badge = TrustBadgeEnum.valueOf(command.badge());
        var current = trustSnapshotRepository.findByAccountId(command.accountId());
        if (current.isPresent() && current.get().getSourceOccurredAt().isAfter(command.occurredAt())) {
            recordConsumed(command.eventId());
            return new Result(command.accountId(), false);
        }

        ProfileTrustSnapshot next = current
                .map(snapshot -> snapshot.updateFromTrustScore(
                        command.score(),
                        level,
                        badge,
                        command.occurredAt()
                ))
                .orElseGet(() -> ProfileTrustSnapshot.fromTrustScore(
                        command.accountId(),
                        command.score(),
                        level,
                        badge,
                        command.occurredAt()
                ));
        trustSnapshotRepository.save(next);
        recordConsumed(command.eventId());
        return new Result(command.accountId(), true);
    }

    private void recordConsumed(java.util.UUID eventId) {
        consumedEventRepository.record(
                eventId,
                TrustEventTypes.TRUST_SCORE_UPDATED_V1,
                Instant.now(clock)
        );
    }
}
