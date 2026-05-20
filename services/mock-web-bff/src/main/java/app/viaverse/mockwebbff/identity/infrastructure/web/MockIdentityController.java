package app.viaverse.mockwebbff.identity.infrastructure.web;

import app.viaverse.mockwebbff.app.AppDtos.AuthSessionView;
import app.viaverse.mockwebbff.app.AppDtos.CapabilityTermsView;
import app.viaverse.mockwebbff.app.AppDtos.ForgotPasswordCompleteRequest;
import app.viaverse.mockwebbff.app.AppDtos.ForgotPasswordStartRequest;
import app.viaverse.mockwebbff.app.AppDtos.ForgotPasswordStartView;
import app.viaverse.mockwebbff.app.AppDtos.ForgotPasswordTokenView;
import app.viaverse.mockwebbff.app.AppDtos.ForgotPasswordVerifyRequest;
import app.viaverse.mockwebbff.app.AppDtos.PasswordLoginRequest;
import app.viaverse.mockwebbff.app.AppDtos.RegisterStartRequest;
import app.viaverse.mockwebbff.app.AppDtos.RegisterStartView;
import app.viaverse.mockwebbff.app.AppDtos.RegisterVerifyEmailRequest;
import app.viaverse.mockwebbff.app.AppDtos.RequiredConsentsView;
import app.viaverse.mockwebbff.app.AppDtos.UserView;
import app.viaverse.mockwebbff.app.MockAppService;
import app.viaverse.mockwebbff.shared.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MockIdentityController {

    private final MockAppService service;

    public MockIdentityController(MockAppService service) {
        this.service = service;
    }

    @GetMapping("/auth/required-consents")
    ApiResponse<RequiredConsentsView> requiredConsents() {
        return ApiResponse.success(service.requiredConsents());
    }

    @GetMapping("/auth/capability-terms")
    ApiResponse<CapabilityTermsView> capabilityTerms() {
        return ApiResponse.success(service.capabilityTerms());
    }

    @PostMapping("/auth/password-login")
    ApiResponse<AuthSessionView> passwordLogin(@RequestBody PasswordLoginRequest request) {
        return ApiResponse.success(service.passwordLogin(request));
    }

    @PostMapping("/auth/register/start")
    ApiResponse<RegisterStartView> registerStart(@RequestBody RegisterStartRequest request) {
        return ApiResponse.success(service.registerStart(request));
    }

    @PostMapping("/auth/register/verify-email")
    ApiResponse<AuthSessionView> registerVerifyEmail(@RequestBody RegisterVerifyEmailRequest request) {
        return ApiResponse.success(service.registerVerifyEmail(request));
    }

    @PostMapping("/auth/refresh")
    ApiResponse<AuthSessionView> refresh() {
        return ApiResponse.success(service.refreshAuth());
    }

    @PostMapping("/auth/logout")
    ApiResponse<Void> logout() {
        service.logoutAuth();
        return ApiResponse.success(null);
    }

    @PostMapping("/auth/forgot-password/start")
    ApiResponse<ForgotPasswordStartView> forgotStart(@RequestBody ForgotPasswordStartRequest request) {
        return ApiResponse.success(service.forgotPasswordStart(request));
    }

    @PostMapping("/auth/forgot-password/verify-otp")
    ApiResponse<ForgotPasswordTokenView> forgotVerify(@RequestBody ForgotPasswordVerifyRequest request) {
        return ApiResponse.success(service.forgotPasswordVerify(request));
    }

    @PostMapping("/auth/forgot-password/complete")
    ApiResponse<Void> forgotComplete(@RequestBody ForgotPasswordCompleteRequest request) {
        service.forgotPasswordComplete(request);
        return ApiResponse.success(null);
    }

    @GetMapping("/me")
    ApiResponse<UserView> me() {
        return ApiResponse.success(service.session().currentUser());
    }
}
