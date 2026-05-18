package app.viaverse.webbff.profile;

import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BffProfileController {

    private static final String PROFILE_SELF_PATH = "/api/v1/me/profile";
    private static final String PROFILE_CAPABILITIES_PATH = "/api/v1/me/capabilities/individual-provider";
    private static final String BUSINESS_CAPABILITIES_PATH = "/api/v1/me/capabilities/business";
    private static final String INDIVIDUAL_PROVIDER_PROFILE_PATH = "/api/v1/me/individual-provider-profile";
    private static final String ACTIVE_MODE_PATH = "/api/v1/me/active-mode";
    private static final String BUSINESS_PROFILE_PATH = "/api/v1/me/business";
    private static final String PROFILE_PREFERENCES_PATH = "/api/v1/me/preferences";
    private static final String PROFILE_BLOCKS_PATH = "/api/v1/me/blocks";
    private static final String PUBLIC_PROFILE_PATH = "/api/v1/profiles/";

    private final ProfileProxy profileProxy;

    public BffProfileController(ProfileProxy profileProxy) {
        this.profileProxy = profileProxy;
    }

    @GetMapping("/me/profile")
    public ResponseEntity<Map<String, Object>> me(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        ProfileProxy.ProxyResponse proxied = profileProxy.get(PROFILE_SELF_PATH, authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }

    @PatchMapping("/me/profile")
    public ResponseEntity<Map<String, Object>> updateMe(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.PATCH, PROFILE_SELF_PATH, body, authorization);
    }

    @PostMapping("/me/capabilities/individual-provider/enable")
    public ResponseEntity<Map<String, Object>> enableIndividualProvider(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.POST, PROFILE_CAPABILITIES_PATH + "/enable", body, authorization);
    }

    @PostMapping("/me/capabilities/individual-provider/disable")
    public ResponseEntity<Map<String, Object>> disableIndividualProvider(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.POST, PROFILE_CAPABILITIES_PATH + "/disable", Map.of(), authorization);
    }

    @PostMapping("/me/capabilities/business/start")
    public ResponseEntity<Map<String, Object>> startBusiness(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.POST, BUSINESS_CAPABILITIES_PATH + "/start", Map.of(), authorization);
    }

    @PostMapping("/me/capabilities/business/submit")
    public ResponseEntity<Map<String, Object>> submitBusiness(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.POST, BUSINESS_CAPABILITIES_PATH + "/submit", body, authorization);
    }

    @GetMapping("/me/business")
    public ResponseEntity<Map<String, Object>> business(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        ProfileProxy.ProxyResponse proxied = profileProxy.get(BUSINESS_PROFILE_PATH, authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }

    @PatchMapping("/me/business/draft")
    public ResponseEntity<Map<String, Object>> updateBusinessDraft(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.PATCH, BUSINESS_PROFILE_PATH + "/draft", body, authorization);
    }

    @GetMapping("/me/individual-provider-profile")
    public ResponseEntity<Map<String, Object>> individualProviderProfile(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        ProfileProxy.ProxyResponse proxied = profileProxy.get(INDIVIDUAL_PROVIDER_PROFILE_PATH, authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }

    @PatchMapping("/me/individual-provider-profile")
    public ResponseEntity<Map<String, Object>> updateIndividualProviderProfile(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.PATCH, INDIVIDUAL_PROVIDER_PROFILE_PATH, body, authorization);
    }

    @PatchMapping("/me/active-mode")
    public ResponseEntity<Map<String, Object>> updateActiveMode(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.PATCH, ACTIVE_MODE_PATH, body, authorization);
    }

    @GetMapping("/profile/{accountId}")
    public ResponseEntity<Map<String, Object>> publicProfile(
            @PathVariable String accountId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        ProfileProxy.ProxyResponse proxied = profileProxy.get(PUBLIC_PROFILE_PATH + accountId, authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }

    @GetMapping("/me/preferences")
    public ResponseEntity<Map<String, Object>> preferences(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        ProfileProxy.ProxyResponse proxied = profileProxy.get(PROFILE_PREFERENCES_PATH, authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }

    @PutMapping("/me/preferences/{key}")
    public ResponseEntity<Map<String, Object>> putPreference(
            @PathVariable String key,
            @RequestBody Object body,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.PUT, PROFILE_PREFERENCES_PATH + "/" + key, body, authorization);
    }

    @GetMapping("/me/blocks")
    public ResponseEntity<Map<String, Object>> blocks(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        ProfileProxy.ProxyResponse proxied = profileProxy.get(PROFILE_BLOCKS_PATH, authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }

    @PostMapping("/me/blocks")
    public ResponseEntity<Map<String, Object>> block(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.POST, PROFILE_BLOCKS_PATH, body, authorization);
    }

    @DeleteMapping("/me/blocks/{blockedAccountId}")
    public ResponseEntity<Map<String, Object>> unblock(
            @PathVariable String blockedAccountId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.DELETE, PROFILE_BLOCKS_PATH + "/" + blockedAccountId, Map.of(), authorization);
    }

    private ResponseEntity<Map<String, Object>> forward(
            HttpMethod method,
            String path,
            Object body,
            String authorization
    ) {
        ProfileProxy.ProxyResponse proxied = profileProxy.exchange(method, path, body, authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }
}
