package app.viaverse.identity.auth.infrastructure.adapter.in.web.controller;

import app.viaverse.identity.auth.application.port.in.CompletePasswordResetUseCase;
import app.viaverse.identity.auth.application.port.in.StartPasswordResetUseCase;
import app.viaverse.identity.auth.application.port.in.VerifyPasswordResetOtpUseCase;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request.ForgotPasswordCompleteRequest;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request.ForgotPasswordStartRequest;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request.ForgotPasswordVerifyOtpRequest;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response.ForgotPasswordStartResponse;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response.ForgotPasswordTokenResponse;
import app.viaverse.web.api.ApiResponse;
import app.viaverse.web.security.ClientIpResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Password-reset (a.k.a. forgot-password) flow. Three-step OTP exchange:
 *
 * <ol>
 *   <li>{@code POST /start} → server dispatches an OTP to the supplied
 *       identifier if it resolves to an active account. Response shape is
 *       identical whether or not the identifier exists, so the endpoint
 *       can't be used to enumerate accounts.</li>
 *   <li>{@code POST /verify-otp} → server validates the OTP and returns a
 *       short-lived {@code resetToken}.</li>
 *   <li>{@code POST /complete} → server validates the new password against
 *       the policy and rotates the account credential. No session is issued;
 *       the user goes through {@code /auth/password-login} on the next
 *       screen so the new credential is exercised end-to-end.</li>
 * </ol>
 */
@RestController
@RequestMapping("/api/v1/auth/forgot-password")
public class ForgotPasswordController {

    private final StartPasswordResetUseCase startPasswordResetUseCase;
    private final VerifyPasswordResetOtpUseCase verifyPasswordResetOtpUseCase;
    private final CompletePasswordResetUseCase completePasswordResetUseCase;
    private final ClientIpResolver clientIpResolver;

    public ForgotPasswordController(
            StartPasswordResetUseCase startPasswordResetUseCase,
            VerifyPasswordResetOtpUseCase verifyPasswordResetOtpUseCase,
            CompletePasswordResetUseCase completePasswordResetUseCase,
            ClientIpResolver clientIpResolver
    ) {
        this.startPasswordResetUseCase = startPasswordResetUseCase;
        this.verifyPasswordResetOtpUseCase = verifyPasswordResetOtpUseCase;
        this.completePasswordResetUseCase = completePasswordResetUseCase;
        this.clientIpResolver = clientIpResolver;
    }

    @PostMapping("/start")
    public ApiResponse<ForgotPasswordStartResponse> start(
            @Valid @RequestBody ForgotPasswordStartRequest request,
            @RequestHeader(value = "X-Client-Fingerprint", required = false) String clientFingerprint,
            HttpServletRequest httpRequest
    ) {
        StartPasswordResetUseCase.Result result = startPasswordResetUseCase.execute(
                new StartPasswordResetUseCase.Command(
                        request.identifier(),
                        clientIpResolver.resolve(httpRequest),
                        clientFingerprint
                )
        );
        return ApiResponse.ok(new ForgotPasswordStartResponse(
                result.flowId(), result.identifierType(), result.expiresAt()));
    }

    @PostMapping("/verify-otp")
    public ApiResponse<ForgotPasswordTokenResponse> verifyOtp(
            @Valid @RequestBody ForgotPasswordVerifyOtpRequest request,
            HttpServletRequest httpRequest
    ) {
        VerifyPasswordResetOtpUseCase.Result result = verifyPasswordResetOtpUseCase.execute(
                new VerifyPasswordResetOtpUseCase.Command(
                        request.flowId(), request.otp(),
                        clientIpResolver.resolve(httpRequest)
                )
        );
        return ApiResponse.ok(new ForgotPasswordTokenResponse(result.resetToken(), result.expiresAt()));
    }

    @PostMapping("/complete")
    public ApiResponse<Void> complete(@Valid @RequestBody ForgotPasswordCompleteRequest request) {
        completePasswordResetUseCase.execute(
                new CompletePasswordResetUseCase.Command(request.resetToken(), request.newPassword())
        );
        return ApiResponse.ok(null);
    }
}

