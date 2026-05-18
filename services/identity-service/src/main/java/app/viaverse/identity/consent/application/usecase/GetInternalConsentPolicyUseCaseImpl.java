package app.viaverse.identity.consent.application.usecase;

import app.viaverse.identity.consent.application.ConsentPolicy;
import app.viaverse.identity.consent.application.port.in.GetInternalConsentPolicyUseCase;
import app.viaverse.web.logging.ObservedAction;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetInternalConsentPolicyUseCaseImpl implements GetInternalConsentPolicyUseCase {

    private final ConsentPolicy consentPolicy;

    public GetInternalConsentPolicyUseCaseImpl(ConsentPolicy consentPolicy) {
        this.consentPolicy = consentPolicy;
    }

    @Override
    @ObservedAction("identity.internal.consent-policy")
    public Result execute() {
        return new Result(List.of(
                consentPolicy.providerTermsDocument(),
                consentPolicy.businessTermsDocument()
        ));
    }
}
