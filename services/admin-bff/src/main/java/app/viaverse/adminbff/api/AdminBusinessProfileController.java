package app.viaverse.adminbff.api;

import app.viaverse.adminbff.application.AdminBusinessProfileProxy;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/business-profiles")
public class AdminBusinessProfileController {

    private final AdminBusinessProfileProxy proxy;

    public AdminBusinessProfileController(AdminBusinessProfileProxy proxy) {
        this.proxy = proxy;
    }

    @GetMapping("/submissions")
    public ResponseEntity<Map<String, Object>> submissions() {
        AdminBusinessProfileProxy.ProxyResponse response = proxy.get("/api/v1/internal/business/submissions");
        return ResponseEntity.status(response.status()).body(response.body());
    }

    @PostMapping("/{accountId}/approve")
    public ResponseEntity<Map<String, Object>> approve(@PathVariable String accountId) {
        AdminBusinessProfileProxy.ProxyResponse response =
                proxy.post("/api/v1/internal/business/" + accountId + "/approve", Map.of());
        return ResponseEntity.status(response.status()).body(response.body());
    }

    @PostMapping("/{accountId}/reject")
    public ResponseEntity<Map<String, Object>> reject(
            @PathVariable String accountId,
            @RequestBody Map<String, Object> body
    ) {
        AdminBusinessProfileProxy.ProxyResponse response =
                proxy.post("/api/v1/internal/business/" + accountId + "/reject", body);
        return ResponseEntity.status(response.status()).body(response.body());
    }
}
