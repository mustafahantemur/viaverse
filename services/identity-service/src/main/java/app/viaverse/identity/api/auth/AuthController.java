package app.viaverse.identity.api.auth;

import app.viaverse.identity.application.auth.AuthResult;
import app.viaverse.identity.application.auth.ConsentInput;
import app.viaverse.identity.application.auth.IdentityAuthService;
import app.viaverse.identity.application.auth.StartAuthResult;
import app.viaverse.identity.domain.auth.ConsentType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final IdentityAuthService identityAuthService;

    public AuthController(IdentityAuthService identityAuthService) {
        this.identityAuthService = identityAuthService;
    }

    @PostMapping("/start")
    public StartAuthResult start(
            @Valid @RequestBody StartAuthRequest request,
            @RequestHeader(value = "X-Client-Fingerprint", required = false) String clientFingerprint,
            HttpServletRequest httpRequest
    ) {
        return identityAuthService.start(request.identifier(), clientIp(httpRequest), clientFingerprint);
    }

    @PostMapping("/verify-otp")
    public Object verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            HttpServletRequest httpRequest
    ) {
        return identityAuthService.verifyOtp(request.flowId(), request.otp(), userAgent, clientIp(httpRequest));
    }

    @PostMapping("/register")
    public AuthResult register(
            @Valid @RequestBody RegisterRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent
    ) {
        return identityAuthService.register(
                request.registrationToken(),
                request.displayName(),
                request.firstName(),
                request.lastName(),
                request.requiredConsents().stream()
                        .map(consent -> new ConsentInput(consent.type(), consent.version()))
                        .toList(),
                request.marketingConsentAccepted(),
                userAgent
        );
    }

    @PostMapping("/refresh")
    public AuthResult refresh(
            @Valid @RequestBody RefreshRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent
    ) {
        return identityAuthService.refresh(request.refreshToken(), userAgent);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody(required = false) LogoutRequest request
    ) {
        identityAuthService.logout(authorization, request == null ? null : request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    public record StartAuthRequest(@NotBlank String identifier) {
    }

    public record VerifyOtpRequest(@NotNull UUID flowId, @NotBlank String otp) {
    }

    public record RegisterRequest(
            @NotBlank String registrationToken,
            @NotBlank String displayName,
            String firstName,
            String lastName,
            @NotEmpty List<@Valid ConsentRequest> requiredConsents,
            boolean marketingConsentAccepted
    ) {
    }

    public record ConsentRequest(@NotNull ConsentType type, @NotBlank String version) {
    }

    public record RefreshRequest(@NotBlank String refreshToken) {
    }

    public record LogoutRequest(String refreshToken) {
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
}
