package app.viaverse.identity.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password hashing setup. Defaults to Argon2id (OWASP 2026 recommendation),
 * but wraps it in {@link DelegatingPasswordEncoder} so any hash written by a
 * future algorithm is automatically detected via its prefix. Existing bcrypt
 * hashes (if we ever ingest legacy data) verify transparently.
 *
 * <p>Parameter choice for Argon2id: {@code salt=16, hash=32, parallelism=1,
 * memory=19 MiB, iterations=2}. Spring Security's defaults for
 * {@code defaultsForSpringSecurity_v5_8} produce roughly 50–80 ms per hash on
 * a modern x86 server, which is the OWASP sweet spot.
 */
@Configuration
public class PasswordConfiguration {

    @Bean
    PasswordEncoder passwordEncoder() {
        Argon2PasswordEncoder argon2 = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("argon2", argon2);
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        DelegatingPasswordEncoder delegating = new DelegatingPasswordEncoder("argon2", encoders);
        delegating.setDefaultPasswordEncoderForMatches(argon2);
        return delegating;
    }
}
