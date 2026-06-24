package app.viaverse.identity.consent.application.port.in;

import app.viaverse.identity.consent.domain.RequiredConsentDocument;
import java.util.List;

public interface GetInternalConsentPolicyUseCase {

    Result execute();

    record Result(List<RequiredConsentDocument> capabilityTerms) {
    }
}
