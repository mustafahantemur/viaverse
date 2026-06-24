package app.viaverse.identity.account.infrastructure.adapter.in.web.controller;

import app.viaverse.identity.account.infrastructure.adapter.in.web.dto.request.TwoFactorConfirmRequest;
import app.viaverse.identity.account.infrastructure.adapter.in.web.dto.request.TwoFactorDisableRequest;
import app.viaverse.identity.account.infrastructure.adapter.in.web.dto.response.TwoFactorBackupCodesResponse;
import app.viaverse.identity.account.infrastructure.adapter.in.web.dto.response.TwoFactorEnrollmentResponse;
import app.viaverse.identity.auth.application.port.in.ConfirmTwoFactorEnrollmentUseCase;
import app.viaverse.identity.auth.application.port.in.DisableTwoFactorUseCase;
import app.viaverse.identity.auth.application.port.in.StartTwoFactorEnrollmentUseCase;
import app.viaverse.identity.auth.infrastructure.security.JwtPrincipal;
import app.viaverse.identity.auth.infrastructure.security.JwtPrincipalResolver;
import app.viaverse.web.api.ApiResponse;
import app.viaverse.web.security.ClientIpResolver;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 2FA enrollment & disablement. Lives under {@code /api/v1/me/2fa} so it
 * implicitly requires an authenticated session (the security filter chain
 * matches {@code /api/v1/me/**}). All three endpoints require fresh
 * proof-of-ownership via OTP to the primary identifier, and confirm/disable
 * additionally require a TOTP code (or backup code for disable) so that a
 * stolen session alone cannot toggle 2FA.
 */
@RestController
@RequestMapping("/api/v1/me/2fa")
@SecurityRequirement(name = "bearerAuth")
public class TwoFactorController {

    private final StartTwoFactorEnrollmentUseCase startEnrollment;
    private final ConfirmTwoFactorEnrollmentUseCase confirmEnrollment;
    private final DisableTwoFactorUseCase disableTwoFactor;
    private final JwtPrincipalResolver jwtPrincipalResolver;
    private final ClientIpResolver clientIpResolver;

    public TwoFactorController(
            StartTwoFactorEnrollmentUseCase startEnrollment,
            ConfirmTwoFactorEnrollmentUseCase confirmEnrollment,
            DisableTwoFactorUseCase disableTwoFactor,
            JwtPrincipalResolver jwtPrincipalResolver,
            ClientIpResolver clientIpResolver
    ) {
        this.startEnrollment = startEnrollment;
        this.confirmEnrollment = confirmEnrollment;
        this.disableTwoFactor = disableTwoFactor;
        this.jwtPrincipalResolver = jwtPrincipalResolver;
        this.clientIpResolver = clientIpResolver;
    }

    @PostMapping("/enroll")
    public ApiResponse<TwoFactorEnrollmentResponse> enroll(
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest
    ) {
        JwtPrincipal principal = jwtPrincipalResolver.resolve(jwt);
        StartTwoFactorEnrollmentUseCase.Result result = startEnrollment.execute(
                new StartTwoFactorEnrollmentUseCase.Command(
                        principal.accountId(), clientIpResolver.resolve(httpRequest))
        );
        return ApiResponse.ok(new TwoFactorEnrollmentResponse(
                result.flowId(),
                result.otpIdentifierType(),
                result.otpIdentifierMasked(),
                result.otpExpiresAt(),
                result.secretBase32(),
                result.provisioningUri()
        ));
    }

    @PostMapping("/confirm")
    public ApiResponse<TwoFactorBackupCodesResponse> confirm(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody TwoFactorConfirmRequest request,
            HttpServletRequest httpRequest
    ) {
        JwtPrincipal principal = jwtPrincipalResolver.resolve(jwt);
        ConfirmTwoFactorEnrollmentUseCase.Result result = confirmEnrollment.execute(
                new ConfirmTwoFactorEnrollmentUseCase.Command(
                        principal.accountId(),
                        request.flowId(),
                        request.otp(),
                        request.totpCode(),
                        clientIpResolver.resolve(httpRequest)
                )
        );
        return ApiResponse.ok(new TwoFactorBackupCodesResponse(result.backupCodes()));
    }

    @DeleteMapping
    public ApiResponse<Void> disable(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody TwoFactorDisableRequest request,
            HttpServletRequest httpRequest
    ) {
        JwtPrincipal principal = jwtPrincipalResolver.resolve(jwt);
        disableTwoFactor.execute(new DisableTwoFactorUseCase.Command(
                principal.accountId(),
                request.flowId(),
                request.otp(),
                request.totpCode(),
                request.backupCode(),
                clientIpResolver.resolve(httpRequest)
        ));
        return ApiResponse.ok(null);
    }
}

