package app.viaverse.identity.infrastructure.otp;

import app.viaverse.identity.application.auth.OtpDeliveryPort;
import app.viaverse.identity.application.auth.OtpDeliveryRequest;
import org.springframework.stereotype.Component;

@Component
public class DebugOtpDeliveryAdapter implements OtpDeliveryPort {
    @Override
    public void deliver(OtpDeliveryRequest request) {
        // Debug/local OTP is returned by the API response when enabled; no external call is made.
    }
}
