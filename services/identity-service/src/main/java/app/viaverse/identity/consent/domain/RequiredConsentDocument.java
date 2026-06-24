package app.viaverse.identity.consent.domain;

/**
 * Server-published consent document. Tells the client which legal text to
 * present, what version to acknowledge, and where to read the full text.
 * The client never picks the version — it always sends back the type.
 */
public record RequiredConsentDocument(
        ConsentTypeEnum type,
        ConsentCategoryEnum category,
        String version,
        String url
) {
}
