package app.viaverse.identity.auth.infrastructure.adapter.in.web.mapper;

import app.viaverse.identity.account.domain.AccountView;
import app.viaverse.identity.auth.application.port.in.CompleteAdminRegistrationUseCase;
import app.viaverse.identity.auth.application.port.in.CompleteRegistrationUseCase;
import app.viaverse.identity.auth.application.port.in.PasswordLoginUseCase;
import app.viaverse.identity.auth.application.port.in.RefreshTokenUseCase;
import app.viaverse.identity.auth.application.port.in.SocialSignInUseCase;
import app.viaverse.identity.auth.application.port.in.StartAuthUseCase;
import app.viaverse.identity.auth.application.port.in.VerifyOtpUseCase;
import app.viaverse.identity.auth.application.port.in.VerifyTotpUseCase;
import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response.AuthCompletionResponse;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response.AuthResponse;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response.RegistrationRequiredResponse;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response.StartAuthResponse;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response.TotpRequiredResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Inbound port → outbound DTO mapping. One-way (port → DTO) so transport
 * concerns stay out of the application layer. The verbose {@code default}
 * methods exist because MapStruct cannot synthesise sealed-interface
 * polymorphism from a single {@code nextStep} field.
 */
@Mapper(componentModel = "spring")
public interface AuthDtoMapper {

    StartAuthResponse toResponse(StartAuthUseCase.Result result);

    default AuthCompletionResponse toResponse(VerifyOtpUseCase.Result result) {
        return new RegistrationRequiredResponse(
                result.nextStep(),
                result.registrationToken(),
                result.registrationExpiresAt()
        );
    }

    default AuthCompletionResponse toResponse(SocialSignInUseCase.Result result) {
        return switch (result.nextStep()) {
            case REGISTRATION_REQUIRED -> new RegistrationRequiredResponse(
                    result.nextStep(), result.registrationToken(), result.registrationExpiresAt());
            case TOTP_REQUIRED -> new TotpRequiredResponse(
                    result.nextStep(), result.partialAuthToken(), result.partialAuthExpiresAt());
            default -> new AuthResponse(
                    result.nextStep(),
                    result.accessToken(),
                    result.accessTokenExpiresAt(),
                    result.refreshToken(),
                    result.refreshTokenExpiresAt(),
                    null);
        };
    }

    default AuthCompletionResponse toResponse(PasswordLoginUseCase.Result result) {
        if (result.nextStep() == AuthNextStepEnum.TOTP_REQUIRED) {
            return new TotpRequiredResponse(
                    result.nextStep(), result.partialAuthToken(), result.partialAuthExpiresAt());
        }
        return new AuthResponse(
                result.nextStep(),
                result.accessToken(),
                result.accessTokenExpiresAt(),
                result.refreshToken(),
                result.refreshTokenExpiresAt(),
                null
        );
    }

    @Mapping(target = "nextStep", expression = "java(app.viaverse.identity.auth.domain.enums.AuthNextStepEnum.AUTHENTICATED)")
    @Mapping(target = "account", ignore = true)
    AuthResponse toResponse(CompleteRegistrationUseCase.Result result);

    @Mapping(target = "nextStep", expression = "java(app.viaverse.identity.auth.domain.enums.AuthNextStepEnum.AUTHENTICATED)")
    @Mapping(target = "account", ignore = true)
    AuthResponse toResponse(CompleteAdminRegistrationUseCase.Result result);

    @Mapping(target = "nextStep", expression = "java(app.viaverse.identity.auth.domain.enums.AuthNextStepEnum.AUTHENTICATED)")
    @Mapping(target = "account", ignore = true)
    AuthResponse toResponse(RefreshTokenUseCase.Result result);

    @Mapping(target = "nextStep", expression = "java(app.viaverse.identity.auth.domain.enums.AuthNextStepEnum.AUTHENTICATED)")
    @Mapping(target = "account", ignore = true)
    AuthResponse toResponse(VerifyTotpUseCase.Result result);

    /** Variant for flows that already loaded {@link AccountView}. */
    default AuthResponse toAuthResponse(SocialSignInUseCase.Result result, AccountView account) {
        return new AuthResponse(
                result.nextStep(),
                result.accessToken(),
                result.accessTokenExpiresAt(),
                result.refreshToken(),
                result.refreshTokenExpiresAt(),
                account
        );
    }

    default AuthResponse toAuthResponse(PasswordLoginUseCase.Result result, AccountView account) {
        return new AuthResponse(
                result.nextStep(),
                result.accessToken(),
                result.accessTokenExpiresAt(),
                result.refreshToken(),
                result.refreshTokenExpiresAt(),
                account
        );
    }

    default AuthResponse toAuthResponse(VerifyTotpUseCase.Result result, AccountView account) {
        return new AuthResponse(
                result.nextStep(),
                result.accessToken(),
                result.accessTokenExpiresAt(),
                result.refreshToken(),
                result.refreshTokenExpiresAt(),
                account
        );
    }

    default AuthResponse toAuthResponse(CompleteRegistrationUseCase.Result result, AccountView account) {
        return new AuthResponse(
                AuthNextStepEnum.AUTHENTICATED,
                result.accessToken(),
                result.accessTokenExpiresAt(),
                result.refreshToken(),
                result.refreshTokenExpiresAt(),
                account
        );
    }

    default AuthResponse toAuthResponse(CompleteAdminRegistrationUseCase.Result result, AccountView account) {
        return new AuthResponse(
                AuthNextStepEnum.AUTHENTICATED,
                result.accessToken(),
                result.accessTokenExpiresAt(),
                result.refreshToken(),
                result.refreshTokenExpiresAt(),
                account
        );
    }
}
