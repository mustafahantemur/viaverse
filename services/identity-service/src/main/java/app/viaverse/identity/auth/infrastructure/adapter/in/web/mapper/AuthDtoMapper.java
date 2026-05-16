package app.viaverse.identity.auth.infrastructure.adapter.in.web.mapper;

import app.viaverse.identity.account.domain.AccountView;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response.AuthResponse;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response.RegistrationRequiredResponse;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response.StartAuthResponse;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response.VerifyOtpResponse;
import app.viaverse.identity.auth.application.port.in.CompleteRegistrationUseCase;
import app.viaverse.identity.auth.application.port.in.RefreshTokenUseCase;
import app.viaverse.identity.auth.application.port.in.StartAuthUseCase;
import app.viaverse.identity.auth.application.port.in.VerifyOtpUseCase;
import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Maps inbound port {@code Result} records to outbound API DTOs. Mapping is
 * intentionally one-way (port -> DTO) so transport concerns never leak into the
 * application layer.
 */
@Mapper(componentModel = "spring")
public interface AuthDtoMapper {

    StartAuthResponse toResponse(StartAuthUseCase.Result result);

    /**
     * Dispatches on {@code nextStep}: the AUTHENTICATED branch maps to an
     * {@link AuthResponse} (with no account info — use cases that need account
     * details should call the richer {@link #toAuthResponse} overload), and
     * REGISTRATION_REQUIRED maps to {@link RegistrationRequiredResponse}.
     */
    default VerifyOtpResponse toResponse(VerifyOtpUseCase.Result result) {
        if (result.nextStep() == AuthNextStepEnum.REGISTRATION_REQUIRED) {
            return new RegistrationRequiredResponse(
                    result.nextStep(),
                    result.registrationToken(),
                    result.registrationExpiresAt()
            );
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
    AuthResponse toResponse(RefreshTokenUseCase.Result result);

    /**
     * Variant used when the caller has already fetched the {@link AccountView}
     * (e.g. controllers that combine session issuance with current-account
     * lookup) and wants it embedded in the response.
     */
    default AuthResponse toAuthResponse(VerifyOtpUseCase.Result result, AccountView account) {
        return new AuthResponse(
                result.nextStep(),
                result.accessToken(),
                result.accessTokenExpiresAt(),
                result.refreshToken(),
                result.refreshTokenExpiresAt(),
                account
        );
    }
}
