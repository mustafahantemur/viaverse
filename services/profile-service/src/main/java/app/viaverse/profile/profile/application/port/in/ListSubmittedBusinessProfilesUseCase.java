package app.viaverse.profile.profile.application.port.in;

import app.viaverse.profile.profile.domain.model.BusinessProfile;
import java.util.List;

public interface ListSubmittedBusinessProfilesUseCase {

    List<BusinessProfile> execute();
}
