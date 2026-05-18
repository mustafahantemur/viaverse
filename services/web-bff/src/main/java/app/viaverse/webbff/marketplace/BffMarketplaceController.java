package app.viaverse.webbff.marketplace;

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
public class BffMarketplaceController {

    private final MarketplaceProxy marketplaceProxy;

    public BffMarketplaceController(MarketplaceProxy marketplaceProxy) {
        this.marketplaceProxy = marketplaceProxy;
    }

    @PostMapping("/requests")
    public ResponseEntity<Map<String, Object>> createRequest(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.POST, "/api/v1/requests", body, authorization);
    }

    @GetMapping("/requests/open")
    public ResponseEntity<Map<String, Object>> openRequests(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        MarketplaceProxy.ProxyResponse proxied = marketplaceProxy.get("/api/v1/requests/open", authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }

    @GetMapping("/feed/work")
    public ResponseEntity<Map<String, Object>> workFeed(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        MarketplaceProxy.ProxyResponse proxied = marketplaceProxy.get("/api/v1/feed/work", authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }

    @GetMapping("/me/requests")
    public ResponseEntity<Map<String, Object>> myRequests(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        MarketplaceProxy.ProxyResponse proxied = marketplaceProxy.get("/api/v1/me/requests", authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }

    @PostMapping("/requests/{requestId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelRequest(
            @PathVariable String requestId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.POST, "/api/v1/requests/" + requestId + "/cancel", Map.of(), authorization);
    }

    @PostMapping("/requests/{requestId}/offers")
    public ResponseEntity<Map<String, Object>> submitOffer(
            @PathVariable String requestId,
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.POST, "/api/v1/requests/" + requestId + "/offers", body, authorization);
    }

    @GetMapping("/requests/{requestId}/offers")
    public ResponseEntity<Map<String, Object>> offers(
            @PathVariable String requestId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        MarketplaceProxy.ProxyResponse proxied = marketplaceProxy.get(
                "/api/v1/requests/" + requestId + "/offers",
                authorization
        );
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }

    @GetMapping("/me/offers")
    public ResponseEntity<Map<String, Object>> myOffers(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        MarketplaceProxy.ProxyResponse proxied = marketplaceProxy.get("/api/v1/me/offers", authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }

    @PostMapping("/offers/{offerId}/withdraw")
    public ResponseEntity<Map<String, Object>> withdrawOffer(
            @PathVariable String offerId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.POST, "/api/v1/offers/" + offerId + "/withdraw", Map.of(), authorization);
    }

    @PostMapping("/requests/{requestId}/offers/{offerId}/accept")
    public ResponseEntity<Map<String, Object>> acceptOffer(
            @PathVariable String requestId,
            @PathVariable String offerId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(
                HttpMethod.POST,
                "/api/v1/requests/" + requestId + "/offers/" + offerId + "/accept",
                Map.of(),
                authorization
        );
    }

    @GetMapping("/me/jobs")
    public ResponseEntity<Map<String, Object>> myJobs(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        MarketplaceProxy.ProxyResponse proxied = marketplaceProxy.get("/api/v1/me/jobs", authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }

    @PostMapping("/jobs/{jobId}/start")
    public ResponseEntity<Map<String, Object>> startJob(
            @PathVariable String jobId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.POST, "/api/v1/jobs/" + jobId + "/start", Map.of(), authorization);
    }

    @PostMapping("/jobs/{jobId}/complete")
    public ResponseEntity<Map<String, Object>> completeJob(
            @PathVariable String jobId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.POST, "/api/v1/jobs/" + jobId + "/complete", Map.of(), authorization);
    }

    private ResponseEntity<Map<String, Object>> forward(
            HttpMethod method,
            String path,
            Object body,
            String authorization
    ) {
        MarketplaceProxy.ProxyResponse proxied = marketplaceProxy.exchange(method, path, body, authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }
}
