package app.viaverse.identity.auth.application.port.out;

import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.domain.value.OtpDeliveryRequest;

/**
 * Outbound port for OTP delivery to a specific identifier channel (phone, email, ...).
 * Multiple implementations coexist; the dispatcher selects the first that
 * {@link #supports(IdentifierTypeEnum)} the request type.
 */
public interface OtpDeliveryPort {

    boolean supports(IdentifierTypeEnum identifierType);

    void deliver(OtpDeliveryRequest request);
}
