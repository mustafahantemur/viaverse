package app.viaverse.identity.auth.infrastructure.adapter.out.otp;

import app.viaverse.identity.auth.application.port.out.OtpDeliveryPort;
import app.viaverse.identity.auth.domain.value.OtpDeliveryRequest;
import org.springframework.stereotype.Component;

@Component
public class DebugOtpDeliveryAdapter implements OtpDeliveryPort {
    @Override
    public void deliver(OtpDeliveryRequest request) {
    }
}
