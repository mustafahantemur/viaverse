package app.viaverse.identity.auth.api;

import app.viaverse.identity.auth.api.dto.AuthResponse;
import app.viaverse.identity.auth.api.dto.LogoutRequest;
import app.viaverse.identity.auth.api.dto.RefreshRequest;
import app.viaverse.identity.auth.api.dto.RegisterRequest;
import app.viaverse.identity.auth.api.dto.StartAuthRequest;
import app.viaverse.identity.auth.api.dto.StartAuthResponse;
import app.viaverse.identity.auth.api.dto.VerifyOtpRequest;
import app.viaverse.identity.auth.api.dto.VerifyOtpResponse;
import app.viaverse.identity.auth.api.mapper.AuthDtoMapper;
import app.viaverse.identity.auth.application.port.in.CompleteRegistrationUseCase;
import app.viaverse.identity.auth.application.port.in.LogoutUseCase;
import app.viaverse.identity.auth.application.port.in.RefreshTokenUseCase;
import app.viaverse.identity.auth.application.port.in.StartAuthUseCase;
import app.viaverse.identity.auth.application.port.in.VerifyOtpUseCase;
import app.viaverse.identity.auth.infrastructure.security.JwtPrincipal;
import app.viaverse.identity.auth.infrastructure.security.JwtPrincipalResolver;
import app.viaverse.identity.consent.domain.ConsentInput;
import app.viaverse.identity.shared.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
    private final JwtPrincipalResolver jwtPrincipalResolver;
    private final AuthDtoMapper authDtoMapper;

    public AuthController(
            StartAuthUseCase startAuthUseCase,
            VerifyOtpUseCase verifyOtpUseCase,
            CompleteRegistrationUseCase completeRegistrationUseCase,
            RefreshTokenUseCase refreshTokenUseCase,
            LogoutUseCase logoutUseCase,
            JwtPrincipalResolver jwtPrincipalResolver,
            AuthDtoMapper authDtoMapper
    ) {
        this.startAuthUseCase = startAuthUseCase;
        this.verifyOtpUseCase = verifyOtpUseCase;
        this.completeRegistrationUseCase = completeRegistrationUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
        this.jwtPrincipalResolver = jwtPrincipalResolver;
        this.authDtoMapper = authDtoMapper;
    }

    @PostMapping("/start")
    public ApiResponse<StartAuthResponse> start(
            @Valid @RequestBody StartAuthRequest request,
            @RequestHeader(value = "X-Client-Fingerprint", required = false) String clientFingerprint,
            HttpServletRequest httpRequest
    ) {
        StartAuthUseCase.Result result = startAuthUseCase.execute(new StartAuthUseCase.Command(
                request.identifier(),
                clientIp(httpRequest),
                clientFingerprint
        ));
        return ApiResponse.ok(authDtoMapper.toResponse(result));
    }

    @PostMapping("/verify-otp")
    public ApiResponse<VerifyOtpResponse> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            HttpServletRequest httpRequest
    ) {
        VerifyOtpUseCase.Result result = verifyOtpUseCase.execute(new VerifyOtpUseCase.Command(
                request.flowId(),
                request.otp(),
                userAgent,
                clientIp(httpRequest)
        ));
        return ApiResponse.ok(authDtoMapper.toResponse(result));
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            HttpServletRequest httpRequest
    ) {
        CompleteRegistrationUseCase.Result result = completeRegistrationUseCase.execute(
                new CompleteRegistrationUseCase.Command(
                        request.registrationToken(),
                        request.displayName(),
                        request.firstName(),
                        request.lastName(),
                        request.requiredConsents().stream()
                                .map(consent -> new ConsentInput(consent.type(), consent.version()))
                                .toList(),
                        request.marketingConsentAccepted(),
                        userAgent,
                        clientIp(httpRequest)
                )
        );
        return ApiResponse.ok(authDtoMapper.toResponse(result));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(
            @Valid @RequestBody RefreshRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            HttpServletRequest httpRequest
    ) {
        RefreshTokenUseCase.Result result = refreshTokenUseCase.execute(new RefreshTokenUseCase.Command(
                request.refreshToken(),
                userAgent,
                clientIp(httpRequest)
        ));
        return ApiResponse.ok(authDtoMapper.toResponse(result));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody(required = false) LogoutRequest request
    ) {
        JwtPrincipal principal = jwt == null ? null : jwtPrincipalResolver.resolve(jwt);
        logoutUseCase.execute(new LogoutUseCase.Command(
                principal == null ? null : principal.accountId(),
                principal == null ? null : principal.sessionId(),
                request == null ? null : request.refreshToken()
        ));
        return ApiResponse.ok(null);
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
