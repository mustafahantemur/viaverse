package app.viaverse.identity.auth.infrastructure.otp;

import app.viaverse.identity.auth.infrastructure.otp.OtpDeliveryPort;
import app.viaverse.identity.auth.domain.value.OtpDeliveryRequest;
import app.viaverse.shared.kernel.error.TechnicalException;

public class SmsOtpDeliveryAdapter implements OtpDeliveryPort {
    @Override
    public void deliver(OtpDeliveryRequest request) {
        throw new TechnicalException("SMS OTP delivery is not implemented yet");
    }
}
