package app.viaverse.identity.consent.infrastructure.adapter.in.web.dto.response;

import app.viaverse.identity.consent.application.port.in.GetInternalConsentPolicyUseCase;
import app.viaverse.identity.consent.domain.ConsentCategoryEnum;
import app.viaverse.identity.consent.domain.ConsentTypeEnum;
import app.viaverse.identity.consent.domain.RequiredConsentDocument;
import java.util.List;

public record InternalConsentPolicyResponse(List<Document> capabilityTerms) {

    public static InternalConsentPolicyResponse from(GetInternalConsentPolicyUseCase.Result result) {
        return new InternalConsentPolicyResponse(result.capabilityTerms().stream()
                .map(Document::from)
                .toList());
    }

    public record Document(
            ConsentTypeEnum type,
            ConsentCategoryEnum category,
            String version,
            String url
    ) {
        public static Document from(RequiredConsentDocument document) {
            return new Document(
                    document.type(),
                    document.category(),
                    document.version(),
                    document.url()
            );
        }
    }
}
