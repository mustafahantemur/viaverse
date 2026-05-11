package app.viaverse.identity.account.api;

import app.viaverse.identity.account.application.CurrentAccountUseCase;
import app.viaverse.identity.account.domain.AccountView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
public class MeController {
    private final CurrentAccountUseCase currentAccountUseCase;

    public MeController(CurrentAccountUseCase currentAccountUseCase) {
        this.currentAccountUseCase = currentAccountUseCase;
    }

    @GetMapping
    public AccountView me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return currentAccountUseCase.currentAccount(authorization);
    }
}
