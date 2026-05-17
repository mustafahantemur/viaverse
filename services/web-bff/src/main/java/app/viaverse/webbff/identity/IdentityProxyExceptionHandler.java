package app.viaverse.webbff.identity;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Application-wide handler: any {@link IdentityProxyException} bubbling out
 * of a controller is mapped to the upstream status + body unchanged, so
 * identity-service stays the source of truth for error shape and the BFF
 * never invents its own error envelope.
 *
 * <p>Lives in its own class (rather than on each controller) so a new
 * controller doesn't have to remember to add the handler.
 */
@RestControllerAdvice
public class IdentityProxyExceptionHandler {

    @ExceptionHandler(IdentityProxyException.class)
    public ResponseEntity<Map<String, Object>> handle(IdentityProxyException exception) {
        return ResponseEntity.status(exception.getStatus()).body(exception.getBody());
    }
}
