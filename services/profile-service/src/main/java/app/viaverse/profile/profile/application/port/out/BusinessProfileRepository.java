package app.viaverse.profile.profile.application.port.out;

import app.viaverse.profile.profile.domain.enums.BusinessVerificationStatusEnum;
import app.viaverse.profile.profile.domain.model.BusinessProfile;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessProfileRepository {

    BusinessProfile save(BusinessProfile profile);

    Optional<BusinessProfile> findByAccountId(UUID accountId);

    List<BusinessProfile> findAllByVerificationStatus(BusinessVerificationStatusEnum status);
}
