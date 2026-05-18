package app.viaverse.marketplace.marketplace.application.usecase;

import app.viaverse.contracts.marketplace.MarketplaceServiceCategory;
import app.viaverse.marketplace.marketplace.application.port.in.ListRelevantServiceRequestsUseCase;
import app.viaverse.marketplace.marketplace.application.port.out.ProviderEligibilityGateway;
import app.viaverse.marketplace.marketplace.application.port.out.ServiceRequestRepository;
import app.viaverse.marketplace.marketplace.domain.model.ServiceRequest;
import app.viaverse.web.logging.ObservedAction;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ListRelevantServiceRequestsUseCaseImpl implements ListRelevantServiceRequestsUseCase {

    private static final String BUSINESS_MODE = "BUSINESS";
    private static final String INDIVIDUAL_PROVIDER_MODE = "INDIVIDUAL_PROVIDER";
    private static final String APPROVED_BUSINESS = "APPROVED";

    private final ServiceRequestRepository repository;
    private final ProviderEligibilityGateway providerEligibilityGateway;

    public ListRelevantServiceRequestsUseCaseImpl(
            ServiceRequestRepository repository,
            ProviderEligibilityGateway providerEligibilityGateway
    ) {
        this.repository = repository;
        this.providerEligibilityGateway = providerEligibilityGateway;
    }

    @Override
    @ObservedAction("marketplace.request.list_relevant")
    public List<ServiceRequest> execute(UUID accountId) {
        ProviderEligibilityGateway.Eligibility eligibility = providerEligibilityGateway.getEligibility(accountId);
        if (!eligibility.canOffer()) {
            return List.of();
        }

        Set<MarketplaceServiceCategory> categories = categoriesFor(eligibility);
        if (categories.isEmpty()) {
            return List.of();
        }
        return repository.findAllOpenByCategories(categories);
    }

    private Set<MarketplaceServiceCategory> categoriesFor(ProviderEligibilityGateway.Eligibility eligibility) {
        if (BUSINESS_MODE.equals(eligibility.activeMode())
                && eligibility.businessEnabled()
                && APPROVED_BUSINESS.equals(eligibility.businessVerificationStatus())) {
            return eligibility.businessServiceCategories();
        }
        if (INDIVIDUAL_PROVIDER_MODE.equals(eligibility.activeMode())
                && eligibility.individualProviderEnabled()) {
            return eligibility.individualProviderServiceCategories();
        }
        return Set.of();
    }
}
