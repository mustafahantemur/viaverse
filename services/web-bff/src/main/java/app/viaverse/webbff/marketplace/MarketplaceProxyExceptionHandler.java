package app.viaverse.webbff.marketplace;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = BffMarketplaceController.class)
public class MarketplaceProxyExceptionHandler {

    @ExceptionHandler(MarketplaceProxyException.class)
    public ResponseEntity<Map<String, Object>> handle(MarketplaceProxyException exception) {
        return ResponseEntity.status(exception.getStatusCode()).body(exception.getBody());
    }
}
