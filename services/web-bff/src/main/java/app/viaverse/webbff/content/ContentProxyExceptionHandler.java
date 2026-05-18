package app.viaverse.webbff.content;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = BffContentController.class)
public class ContentProxyExceptionHandler {
    @ExceptionHandler(ContentProxyException.class)
    public ResponseEntity<Map<String, Object>> handle(ContentProxyException exception) {
        return ResponseEntity.status(exception.getStatusCode()).body(exception.getBody());
    }
}
