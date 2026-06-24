package app.viaverse.identity.support;

import app.viaverse.identity.auth.application.port.out.OtpDeliveryPort;
import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.domain.value.OtpDeliveryRequest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Captures every OTP issued in-memory so integration tests can pull the
 * plaintext code without depending on Mailpit / NetGSM / etc. Last OTP per
 * identifier wins.
 *
 * <p>Registered as the highest-priority {@link OtpDeliveryPort} so it always
 * wins regardless of what else is wired. Does not log or send anything.
 */
public class RecordingOtpDeliveryAdapter implements OtpDeliveryPort {

    private final ConcurrentMap<String, String> latestByIdentifier = new ConcurrentHashMap<>();

    @Override
    public boolean supports(IdentifierTypeEnum identifierType) {
        return true;
    }

    @Override
    public void deliver(OtpDeliveryRequest request) {
        latestByIdentifier.put(key(request.identifier().type(), request.identifier().value()), request.otp());
    }

    public String latestFor(IdentifierTypeEnum type, String normalizedValue) {
        String otp = latestByIdentifier.get(key(type, normalizedValue));
        if (otp == null) {
            throw new IllegalStateException("No OTP recorded for " + type + " " + normalizedValue);
        }
        return otp;
    }

    public void clear() {
        latestByIdentifier.clear();
    }

    private String key(IdentifierTypeEnum type, String value) {
        return type.name() + ":" + value;
    }
}
