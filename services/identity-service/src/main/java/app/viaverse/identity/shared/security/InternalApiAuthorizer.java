package app.viaverse.identity.shared.security;

import app.viaverse.identity.config.AuthProperties;
import app.viaverse.shared.kernel.error.ForbiddenException;
import org.springframework.stereotype.Component;

@Component
public class InternalApiAuthorizer {

    private final AuthProperties properties;

    public InternalApiAuthorizer(AuthProperties properties) {
        this.properties = properties;
    }

    public void requireAuthorized(String suppliedToken) {
        String expected = properties.getInternal().getApiToken();
        if (expected == null || expected.isBlank() || !expected.equals(suppliedToken)) {
            throw new ForbiddenException("Internal API token is invalid");
        }
    }
}
