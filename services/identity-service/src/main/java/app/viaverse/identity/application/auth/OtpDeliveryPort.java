package app.viaverse.identity.application.auth;

public interface OtpDeliveryPort {
    void deliver(OtpDeliveryRequest request);
}
