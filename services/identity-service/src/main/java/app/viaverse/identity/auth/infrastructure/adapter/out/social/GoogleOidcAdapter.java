package app.viaverse.identity.auth.infrastructure.adapter.out.social;

import app.viaverse.identity.auth.domain.enums.SocialAuthProviderEnum;
import app.viaverse.identity.config.AuthProperties;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "viaverse.auth.social.google", name = "enabled", havingValue = "true")
public class GoogleOidcAdapter extends AbstractOidcSocialAuthAdapter {

    private static final String ISSUER = "https://accounts.google.com";
    private static final String JWK_SET_URI = "https://www.googleapis.com/oauth2/v3/certs";

    public GoogleOidcAdapter(AuthProperties properties) {
        super(
                SocialAuthProviderEnum.GOOGLE,
                properties.getSocial().getGoogle().getClientId(),
                List.of(ISSUER),
                decoder()
        );
    }

    private static NimbusJwtDecoder decoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(JWK_SET_URI).build();
        decoder.setJwtValidator(JwtValidators.createDefault());
        return decoder;
    }
}
