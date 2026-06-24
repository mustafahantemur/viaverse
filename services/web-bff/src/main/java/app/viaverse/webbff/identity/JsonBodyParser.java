package app.viaverse.webbff.identity;

import java.util.Map;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/**
 * Parses a raw JSON string into the {@code Map<String, Object>} shape the BFF
 * proxies pass through. Centralised so identity-service error bodies and
 * successful payloads share one parser instance and there's a single place
 * to swap implementations (e.g. to typed records) later.
 */
@Component
public class JsonBodyParser {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final JsonMapper jsonMapper;

    public JsonBodyParser(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public Map<String, Object> parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return Map.of();
        }
        try {
            return jsonMapper.readValue(raw, MAP_TYPE);
        } catch (Exception exception) {
            // Upstream sent something that isn't JSON — surface the raw bytes
            // so the caller can still see what went wrong instead of swallowing it.
            return Map.of("raw", raw);
        }
    }
}
