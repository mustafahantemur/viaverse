package app.viaverse.identity.auth.application.port.out;

import app.viaverse.identity.auth.domain.value.OtpDeliveryRequest;

public interface OtpDeliveryPort {

    void deliver(OtpDeliveryRequest request);
}
