package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.UpdateBusinessDraftUseCase;
import app.viaverse.profile.profile.application.port.out.BusinessProfileRepository;
import app.viaverse.profile.profile.domain.model.BusinessProfile;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateBusinessDraftUseCaseImpl implements UpdateBusinessDraftUseCase {

    private final BusinessProfileRepository repository;
    private final Clock clock;

    public UpdateBusinessDraftUseCaseImpl(BusinessProfileRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    @ObservedAction("profile.business.draft.update")
    @Transactional
    public BusinessProfile execute(Command command) {
        BusinessProfile current = repository.findByAccountId(command.accountId())
                .orElseThrow(() -> new NotFoundException("Business profile not found"));
        return repository.save(current.updateDraft(
                command.legalName(),
                command.tradeName(),
                command.sector(),
                command.taxId(),
                command.addressLine(),
                command.district(),
                command.city(),
                command.country(),
                command.phone(),
                command.emailPublic(),
                command.logoMediaId(),
                command.openingHoursJson(),
                command.serviceCategories(),
                clock.instant()
        ));
    }
}
