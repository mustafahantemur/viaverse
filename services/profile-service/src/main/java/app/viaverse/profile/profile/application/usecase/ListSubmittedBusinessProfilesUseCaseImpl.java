package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.ListSubmittedBusinessProfilesUseCase;
import app.viaverse.profile.profile.application.port.out.BusinessProfileRepository;
import app.viaverse.profile.profile.domain.enums.BusinessVerificationStatusEnum;
import app.viaverse.profile.profile.domain.model.BusinessProfile;
import app.viaverse.web.logging.ObservedAction;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListSubmittedBusinessProfilesUseCaseImpl implements ListSubmittedBusinessProfilesUseCase {

    private final BusinessProfileRepository repository;

    public ListSubmittedBusinessProfilesUseCaseImpl(BusinessProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    @ObservedAction("profile.business.submissions.list")
    public List<BusinessProfile> execute() {
        return repository.findAllByVerificationStatus(BusinessVerificationStatusEnum.SUBMITTED);
    }
}
