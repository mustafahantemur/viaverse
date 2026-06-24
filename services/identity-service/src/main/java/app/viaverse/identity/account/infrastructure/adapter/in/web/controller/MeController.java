package app.viaverse.identity.account.infrastructure.adapter.in.web.controller;

import app.viaverse.identity.account.application.port.in.GetCurrentAccountUseCase;
import app.viaverse.identity.account.domain.AccountView;
import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.identity.account.infrastructure.adapter.in.web.dto.request.ChangePasswordRequest;
import app.viaverse.identity.account.infrastructure.adapter.in.web.mapper.AccountDtoMapper;
import app.viaverse.identity.auth.application.port.in.ChangePasswordUseCase;
import app.viaverse.identity.auth.infrastructure.security.JwtPrincipal;
import app.viaverse.identity.auth.infrastructure.security.JwtPrincipalResolver;
import app.viaverse.web.api.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
@SecurityRequirement(name = "bearerAuth")
public class MeController {

    private final GetCurrentAccountUseCase getCurrentAccountUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final JwtPrincipalResolver jwtPrincipalResolver;
    private final AccountDtoMapper accountDtoMapper;

    public MeController(
            GetCurrentAccountUseCase getCurrentAccountUseCase,
            ChangePasswordUseCase changePasswordUseCase,
            JwtPrincipalResolver jwtPrincipalResolver,
            AccountDtoMapper accountDtoMapper
    ) {
        this.getCurrentAccountUseCase = getCurrentAccountUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.jwtPrincipalResolver = jwtPrincipalResolver;
        this.accountDtoMapper = accountDtoMapper;
    }

    @GetMapping
    public ApiResponse<AccountView> me(@AuthenticationPrincipal Jwt jwt) {
        JwtPrincipal principal = jwtPrincipalResolver.resolve(jwt);
        Account account = getCurrentAccountUseCase.execute(
                new GetCurrentAccountUseCase.Command(principal.accountId(), principal.sessionId())
        );
        return ApiResponse.ok(accountDtoMapper.toView(account));
    }

    @PostMapping("/password")
    public ApiResponse<Void> changePassword(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        JwtPrincipal principal = jwtPrincipalResolver.resolve(jwt);
        changePasswordUseCase.execute(new ChangePasswordUseCase.Command(
                principal.accountId(),
                request.currentPassword(),
                request.newPassword()
        ));
        return ApiResponse.ok(null);
    }
}

