package app.viaverse.trustgamification.trust.application.port.out;

import app.viaverse.trustgamification.trust.domain.model.TrustState;
import java.util.Optional;
import java.util.UUID;

public interface TrustStateRepository {

    TrustState save(TrustState trustState);

    Optional<TrustState> findByAccountId(UUID accountId);
}
