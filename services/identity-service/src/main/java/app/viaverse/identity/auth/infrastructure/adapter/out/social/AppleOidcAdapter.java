package app.viaverse.identity.auth.infrastructure.adapter.out.social;

import app.viaverse.identity.auth.domain.enums.SocialAuthProviderEnum;
import app.viaverse.identity.config.AuthProperties;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "viaverse.auth.social.apple", name = "enabled", havingValue = "true")
public class AppleOidcAdapter extends AbstractOidcSocialAuthAdapter {

    private static final String ISSUER = "https://appleid.apple.com";
    private static final String JWK_SET_URI = "https://appleid.apple.com/auth/keys";

    public AppleOidcAdapter(AuthProperties properties) {
        super(
                SocialAuthProviderEnum.APPLE,
                properties.getSocial().getApple().getClientId(),
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
