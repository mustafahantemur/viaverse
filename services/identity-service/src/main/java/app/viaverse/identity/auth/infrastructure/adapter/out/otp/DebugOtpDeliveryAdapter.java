package app.viaverse.identity.auth.infrastructure.adapter.out.otp;

import app.viaverse.identity.auth.application.port.out.OtpDeliveryPort;
import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.domain.value.OtpDeliveryRequest;

/**
 * No-op delivery adapter for local/test profiles. Claims support for every
 * identifier channel so that any flow can run end-to-end without a real
 * SMS / SMTP backend.
 */
public class DebugOtpDeliveryAdapter implements OtpDeliveryPort {

    @Override
    public boolean supports(IdentifierTypeEnum identifierType) {
        return true;
    }

    @Override
    public void deliver(OtpDeliveryRequest request) {
    }
}
