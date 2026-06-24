package app.viaverse.profile.profile.application.port.out;

import app.viaverse.profile.profile.domain.model.IndividualProviderProfile;
import java.util.Optional;
import java.util.UUID;

public interface IndividualProviderProfileRepository {

    IndividualProviderProfile save(IndividualProviderProfile profile);

    Optional<IndividualProviderProfile> findByAccountId(UUID accountId);
}
