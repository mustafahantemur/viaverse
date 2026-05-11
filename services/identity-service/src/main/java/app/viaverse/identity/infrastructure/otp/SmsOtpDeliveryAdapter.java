package app.viaverse.identity.infrastructure.otp;

import app.viaverse.identity.application.auth.OtpDeliveryPort;
import app.viaverse.identity.application.auth.OtpDeliveryRequest;
import app.viaverse.shared.kernel.error.TechnicalException;

public class SmsOtpDeliveryAdapter implements OtpDeliveryPort {
    @Override
    public void deliver(OtpDeliveryRequest request) {
        throw new TechnicalException("SMS OTP delivery is not implemented yet");
    }
}
