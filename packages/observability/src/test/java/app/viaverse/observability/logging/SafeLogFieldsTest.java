package app.viaverse.observability.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class SafeLogFieldsTest {
    @Test
    void masksEmailWithoutRawAddress() {
        String masked = SafeLogFields.maskIdentifier("Ada.Lovelace@example.com");

        assertEquals("a***@e***com", masked);
        assertFalse(masked.contains("Ada.Lovelace"));
        assertFalse(masked.contains("example.com"));
    }

    @Test
    void masksPhoneWithoutRawNumber() {
        String masked = SafeLogFields.maskIdentifier("+90 555 123 4567");

        assertEquals("***4567", masked);
        assertFalse(masked.contains("555123"));
    }

    @Test
    void keepsBlankValuesNull() {
        assertNull(SafeLogFields.maskIdentifier(" "));
    }
}
