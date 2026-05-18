package app.viaverse.profile.profile.application.port.in;

import app.viaverse.profile.profile.domain.model.IndividualProviderProfile;
import java.util.UUID;

public interface GetCurrentIndividualProviderProfileUseCase {

    IndividualProviderProfile execute(UUID accountId);
}
