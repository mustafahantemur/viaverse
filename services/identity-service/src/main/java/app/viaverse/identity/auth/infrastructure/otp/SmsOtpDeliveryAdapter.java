package app.viaverse.identity.auth.infrastructure.otp;

import app.viaverse.identity.auth.application.port.out.OtpDeliveryPort;
import app.viaverse.identity.auth.domain.value.OtpDeliveryRequest;
import app.viaverse.identity.shared.error.IdentityErrors;

public class SmsOtpDeliveryAdapter implements OtpDeliveryPort {
    @Override
    public void deliver(OtpDeliveryRequest request) {
        throw IdentityErrors.smsProviderDisabled();
    }
}
