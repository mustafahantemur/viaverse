package app.viaverse.profile.profile.application.port.in;

import app.viaverse.profile.profile.domain.model.BusinessProfile;
import java.util.UUID;

public interface GetCurrentBusinessProfileUseCase {

    BusinessProfile execute(UUID accountId);
}
