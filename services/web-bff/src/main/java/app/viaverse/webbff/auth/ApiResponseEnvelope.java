package app.viaverse.webbff.auth;

import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Identity service wraps every successful payload in
 * {@code { "success": true, "data": { … } }}. This helper unwraps that
 * envelope so cookie / header logic can reach into the canonical payload
 * without re-implementing the same {@code Map} hops in every controller.
 *
 * <p>Kept as a Spring bean (rather than a static utility) so it can grow
 * into a typed envelope later — e.g. records + a {@code Result<T>} type —
 * without re-plumbing call sites.
 */
@Component
public class ApiResponseEnvelope {

    @SuppressWarnings("unchecked")
    public Optional<Map<String, Object>> data(Map<String, Object> body) {
        if (body == null) {
            return Optional.empty();
        }
        Object data = body.get("data");
        if (data instanceof Map<?, ?> map) {
            return Optional.of((Map<String, Object>) map);
        }
        return Optional.of(body);
    }

    /**
     * Pull a string field out of the unwrapped data payload, or
     * {@link Optional#empty()} if it's missing / blank / non-string.
     */
    public Optional<String> stringField(Map<String, Object> body, String fieldName) {
        return data(body)
                .map(payload -> payload.get(fieldName))
                .filter(value -> value instanceof String)
                .map(value -> ((String) value).trim())
                .filter(text -> !text.isEmpty());
    }
}
