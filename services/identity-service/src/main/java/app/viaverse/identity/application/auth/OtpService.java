package app.viaverse.identity.application.auth;

import app.viaverse.identity.config.AuthProperties;
import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class OtpService {
    private final AuthProperties properties;
    private final SecureRandom secureRandom;
    private final OtpDeliveryPort deliveryPort;

    public OtpService(AuthProperties properties, SecureRandom secureRandom, OtpDeliveryPort deliveryPort) {
        this.properties = properties;
        this.secureRandom = secureRandom;
        this.deliveryPort = deliveryPort;
    }

    public String generate() {
        if (properties.getDebug().isEnabled()) {
            return properties.getDebug().getFixedOtp();
        }
        return String.format("%06d", secureRandom.nextInt(1_000_000));
    }

    public String debugOtp(String otp) {
        return properties.getDebug().isEnabled() ? otp : null;
    }

    public void deliver(OtpDeliveryRequest request) {
        deliveryPort.deliver(request);
    }
}
