package app.viaverse.security.identity;

import java.util.List;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

public final class RotatingJwtDecoder implements JwtDecoder {

    private final List<JwtDecoder> delegates;

    public RotatingJwtDecoder(List<JwtDecoder> delegates) {
        this.delegates = List.copyOf(delegates);
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        JwtException lastFailure = null;
        for (JwtDecoder delegate : delegates) {
            try {
                return delegate.decode(token);
            } catch (JwtException exception) {
                lastFailure = exception;
            }
        }
        throw lastFailure == null ? new JwtException("No JWT decoders configured") : lastFailure;
    }
}
