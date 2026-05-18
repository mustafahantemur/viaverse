package app.viaverse.webbff.media;

import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BffMediaController {
    private final MediaProxy mediaProxy;

    public BffMediaController(MediaProxy mediaProxy) {
        this.mediaProxy = mediaProxy;
    }

    @PostMapping("/assets/upload-sessions")
    public ResponseEntity<Map<String, Object>> createUploadSession(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.POST, "/api/v1/assets/upload-sessions", body, authorization);
    }

    @PostMapping("/assets/{assetId}/complete")
    public ResponseEntity<Map<String, Object>> completeUpload(
            @PathVariable String assetId,
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.POST, "/api/v1/assets/" + assetId + "/complete", body, authorization);
    }

    @GetMapping("/me/assets")
    public ResponseEntity<Map<String, Object>> myAssets(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        MediaProxy.ProxyResponse proxied = mediaProxy.get("/api/v1/me/assets", authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }

    private ResponseEntity<Map<String, Object>> forward(
            HttpMethod method,
            String path,
            Object body,
            String authorization
    ) {
        MediaProxy.ProxyResponse proxied = mediaProxy.exchange(method, path, body, authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }
}
