package app.viaverse.profile.config;

import app.viaverse.shared.kernel.error.ForbiddenException;
import org.springframework.stereotype.Component;

@Component
public class ProfileInternalApiAuthorizer {

    private final ProfileInternalProperties properties;

    public ProfileInternalApiAuthorizer(ProfileInternalProperties properties) {
        this.properties = properties;
    }

    public void requireAuthorized(String suppliedToken) {
        String expected = properties.getApiToken();
        if (expected == null || expected.isBlank() || !expected.equals(suppliedToken)) {
            throw new ForbiddenException("Internal API token is invalid");
        }
    }
}
