package app.viaverse.identity.auth.infrastructure.otp;

import app.viaverse.identity.auth.domain.value.OtpDeliveryRequest;
import org.springframework.stereotype.Component;

@Component
public class DebugOtpDeliveryAdapter implements OtpDeliveryPort {
    @Override
    public void deliver(OtpDeliveryRequest request) {
        // Debug/local OTP is returned by the API response when enabled; no external call is made.
    }
}
