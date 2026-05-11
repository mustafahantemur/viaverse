package app.viaverse.identity.infrastructure.security;

import app.viaverse.shared.kernel.error.TechnicalException;
import app.viaverse.shared.kernel.error.UnauthorizedException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class JwtTokenService {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final byte[] secret;
    private final Duration accessTokenTtl;

    public JwtTokenService(String secret, Duration accessTokenTtl) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.accessTokenTtl = accessTokenTtl;
    }

    public String issue(UUID accountId, UUID sessionId, Instant now) {
        Instant expiresAt = now.plus(accessTokenTtl);
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("iss", "viaverse-identity");
        payload.put("sub", accountId.toString());
        payload.put("sid", sessionId.toString());
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", expiresAt.getEpochSecond());

        String unsignedToken = base64Json(header) + "." + base64Json(payload);
        return unsignedToken + "." + sign(unsignedToken);
    }

    public JwtPrincipal verify(String token, Instant now) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new UnauthorizedException("Invalid access token");
        }
        String unsignedToken = parts[0] + "." + parts[1];
        if (!sign(unsignedToken).equals(parts[2])) {
            throw new UnauthorizedException("Invalid access token");
        }

        try {
            byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
            Map<String, Object> payload = OBJECT_MAPPER.readValue(payloadBytes, new TypeReference<>() {
            });
            Number expiresAt = (Number) payload.get("exp");
            if (expiresAt == null || Instant.ofEpochSecond(expiresAt.longValue()).isBefore(now)) {
                throw new UnauthorizedException("Access token expired");
            }
            return new JwtPrincipal(
                    UUID.fromString((String) payload.get("sub")),
                    UUID.fromString((String) payload.get("sid"))
            );
        } catch (UnauthorizedException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new UnauthorizedException("Invalid access token");
        }
    }

    public long expiresInSeconds() {
        return accessTokenTtl.toSeconds();
    }

    private String base64Json(Map<String, Object> value) {
        try {
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(OBJECT_MAPPER.writeValueAsBytes(value));
        } catch (Exception exception) {
            throw new TechnicalException("Unable to serialize JWT", exception);
        }
    }

    private String sign(String unsignedToken) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new TechnicalException("Unable to sign JWT", exception);
        }
    }
}
