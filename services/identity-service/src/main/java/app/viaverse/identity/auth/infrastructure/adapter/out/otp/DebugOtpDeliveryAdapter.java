package app.viaverse.identity.auth.infrastructure.adapter.out.otp;

import app.viaverse.identity.auth.application.port.out.OtpDeliveryPort;
import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.domain.value.OtpDeliveryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * No-op delivery adapter for local/test profiles. Logs the OTP at INFO level so
 * developers can copy it from the console / OpenSearch — production must never
 * activate this adapter (guarded by {@code AuthConfiguration.validateConfiguration}).
 */
public class DebugOtpDeliveryAdapter implements OtpDeliveryPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebugOtpDeliveryAdapter.class);

    @Override
    public boolean supports(IdentifierTypeEnum identifierType) {
        return true;
    }

    @Override
    public void deliver(OtpDeliveryRequest request) {
        LOGGER.info(
                "DEBUG OTP for flow {} → identifier={} otp={} expiresAt={}",
                request.flowId(),
                request.identifier().value(),
                request.otp(),
                request.expiresAt()
        );
    }
}
