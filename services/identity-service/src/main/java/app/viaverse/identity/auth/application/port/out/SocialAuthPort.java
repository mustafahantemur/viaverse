package app.viaverse.identity.auth.application.port.out;

import app.viaverse.identity.auth.domain.enums.SocialAuthProviderEnum;
import app.viaverse.identity.auth.domain.value.SocialIdentity;

public interface SocialAuthPort {

    boolean supports(SocialAuthProviderEnum provider);

    SocialIdentity verify(String idToken, String expectedNonce);
}
