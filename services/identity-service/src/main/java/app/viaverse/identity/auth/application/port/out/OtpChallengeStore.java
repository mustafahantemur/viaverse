package app.viaverse.identity.auth.application.port.out;

import app.viaverse.identity.auth.domain.model.OtpChallenge;
import java.util.Optional;
import java.util.UUID;

public interface OtpChallengeStore {

    void save(OtpChallenge challenge);

    Optional<OtpChallenge> findByFlowId(UUID flowId);

    void delete(UUID flowId);
}
