package app.viaverse.identity.account.api;

import app.viaverse.identity.account.application.usecase.CurrentAccountUseCase;
import app.viaverse.identity.account.domain.AccountView;
import app.viaverse.identity.auth.infrastructure.security.JwtPrincipalResolver;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
public class MeController {
    private final CurrentAccountUseCase currentAccountUseCase;
    private final JwtPrincipalResolver jwtPrincipalResolver;

    public MeController(CurrentAccountUseCase currentAccountUseCase, JwtPrincipalResolver jwtPrincipalResolver) {
        this.currentAccountUseCase = currentAccountUseCase;
        this.jwtPrincipalResolver = jwtPrincipalResolver;
    }

    @GetMapping
    public AccountView me(@AuthenticationPrincipal Jwt jwt) {
        return currentAccountUseCase.currentAccount(jwtPrincipalResolver.resolve(jwt));
    }
}
