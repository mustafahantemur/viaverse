package app.viaverse.webbff.content;

import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api")
public class BffContentController {
    private final ContentProxy contentProxy;

    public BffContentController(ContentProxy contentProxy) {
        this.contentProxy = contentProxy;
    }

    @PostMapping("/posts")
    public ResponseEntity<Map<String, Object>> createPost(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.POST, "/api/v1/posts", body, authorization);
    }

    @GetMapping("/posts/published")
    public ResponseEntity<Map<String, Object>> publishedPosts(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/api/v1/posts/published");
        if (city != null && !city.isBlank()) {
            builder.queryParam("city", city);
        }
        if (district != null && !district.isBlank()) {
            builder.queryParam("district", district);
        }
        String path = builder.build().encode().toUriString();
        ContentProxy.ProxyResponse proxied = contentProxy.get(path, authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }

    @GetMapping("/me/posts")
    public ResponseEntity<Map<String, Object>> myPosts(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        ContentProxy.ProxyResponse proxied = contentProxy.get("/api/v1/me/posts", authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }

    private ResponseEntity<Map<String, Object>> forward(
            HttpMethod method,
            String path,
            Object body,
            String authorization
    ) {
        ContentProxy.ProxyResponse proxied = contentProxy.exchange(method, path, body, authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }
}
