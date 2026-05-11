package app.viaverse.identity.auth.api;

import app.viaverse.identity.auth.api.dto.LogoutRequest;
import app.viaverse.identity.auth.api.dto.RefreshRequest;
import app.viaverse.identity.auth.api.dto.RegisterRequest;
import app.viaverse.identity.auth.api.dto.StartAuthRequest;
import app.viaverse.identity.auth.api.dto.AuthResponse;
import app.viaverse.identity.auth.api.dto.VerifyOtpRequest;
import app.viaverse.identity.auth.api.dto.VerifyOtpResponse;
import app.viaverse.identity.auth.application.CompleteRegistrationUseCase;
import app.viaverse.identity.auth.application.LogoutUseCase;
import app.viaverse.identity.auth.application.RefreshTokenUseCase;
import app.viaverse.identity.auth.application.StartAuthUseCase;
import app.viaverse.identity.auth.application.VerifyOtpUseCase;
import app.viaverse.identity.consent.domain.ConsentInput;
import app.viaverse.identity.auth.api.dto.StartAuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final StartAuthUseCase startAuthUseCase;
    private final VerifyOtpUseCase verifyOtpUseCase;
    private final CompleteRegistrationUseCase completeRegistrationUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;

    public AuthController(
            StartAuthUseCase startAuthUseCase,
            VerifyOtpUseCase verifyOtpUseCase,
            CompleteRegistrationUseCase completeRegistrationUseCase,
            RefreshTokenUseCase refreshTokenUseCase,
            LogoutUseCase logoutUseCase
    ) {
        this.startAuthUseCase = startAuthUseCase;
        this.verifyOtpUseCase = verifyOtpUseCase;
        this.completeRegistrationUseCase = completeRegistrationUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
    }

    @PostMapping("/start")
    public StartAuthResponse start(
            @Valid @RequestBody StartAuthRequest request,
            @RequestHeader(value = "X-Client-Fingerprint", required = false) String clientFingerprint,
            HttpServletRequest httpRequest
    ) {
        return startAuthUseCase.start(request.identifier(), clientIp(httpRequest), clientFingerprint);
    }

    @PostMapping("/verify-otp")
    public VerifyOtpResponse verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            HttpServletRequest httpRequest
    ) {
        return verifyOtpUseCase.verify(request.flowId(), request.otp(), userAgent, clientIp(httpRequest));
    }

    @PostMapping("/register")
    public AuthResponse register(
            @Valid @RequestBody RegisterRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent
    ) {
        return completeRegistrationUseCase.complete(
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
    public AuthResponse refresh(
            @Valid @RequestBody RefreshRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent
    ) {
        return refreshTokenUseCase.refresh(request.refreshToken(), userAgent);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody(required = false) LogoutRequest request
    ) {
        logoutUseCase.logout(authorization, request == null ? null : request.refreshToken());
        return ResponseEntity.noContent().build();
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
