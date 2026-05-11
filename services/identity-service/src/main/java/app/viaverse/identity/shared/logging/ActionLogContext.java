package app.viaverse.identity.shared.logging;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ActionLogContext {
    private static final ThreadLocal<Map<String, Object>> FIELDS = ThreadLocal.withInitial(LinkedHashMap::new);

    private ActionLogContext() {
    }

    public static void put(String key, Object value) {
        if (key == null || key.isBlank() || value == null) {
            return;
        }
        FIELDS.get().put(key, value);
    }

    public static Map<String, Object> snapshot() {
        return Map.copyOf(FIELDS.get());
    }

    public static void clear() {
        FIELDS.remove();
    }
}
