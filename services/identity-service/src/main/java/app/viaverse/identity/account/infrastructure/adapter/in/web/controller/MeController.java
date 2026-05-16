package app.viaverse.identity.account.infrastructure.adapter.in.web.controller;

import app.viaverse.identity.account.infrastructure.adapter.in.web.mapper.AccountDtoMapper;
import app.viaverse.identity.account.application.port.in.GetCurrentAccountUseCase;
import app.viaverse.identity.account.domain.AccountView;
import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.identity.auth.infrastructure.security.JwtPrincipal;
import app.viaverse.identity.auth.infrastructure.security.JwtPrincipalResolver;
import app.viaverse.identity.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
@SecurityRequirement(name = "bearerAuth")
public class MeController {

    private final GetCurrentAccountUseCase getCurrentAccountUseCase;
    private final JwtPrincipalResolver jwtPrincipalResolver;
    private final AccountDtoMapper accountDtoMapper;

    public MeController(
            GetCurrentAccountUseCase getCurrentAccountUseCase,
            JwtPrincipalResolver jwtPrincipalResolver,
            AccountDtoMapper accountDtoMapper
    ) {
        this.getCurrentAccountUseCase = getCurrentAccountUseCase;
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
}
