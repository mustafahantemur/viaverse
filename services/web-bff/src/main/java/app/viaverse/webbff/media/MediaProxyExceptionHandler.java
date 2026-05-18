package app.viaverse.webbff.media;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = BffMediaController.class)
public class MediaProxyExceptionHandler {
    @ExceptionHandler(MediaProxyException.class)
    public ResponseEntity<Map<String, Object>> handle(MediaProxyException exception) {
        return ResponseEntity.status(exception.getStatusCode()).body(exception.getBody());
    }
}
