package app.viaverse.identity.auth.infrastructure.otp;

import app.viaverse.identity.auth.domain.value.OtpDeliveryRequest;

public interface OtpDeliveryPort {
    void deliver(OtpDeliveryRequest request);
}
