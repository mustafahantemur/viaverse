package app.viaverse.webbff.profile;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProfileProxyExceptionHandler {

    @ExceptionHandler(ProfileProxyException.class)
    public ResponseEntity<Map<String, Object>> handle(ProfileProxyException exception) {
        return ResponseEntity.status(exception.getStatus()).body(exception.getBody());
    }
}
