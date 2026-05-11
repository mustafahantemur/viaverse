package app.viaverse.identity.api.me;

import app.viaverse.identity.application.auth.AccountView;
import app.viaverse.identity.application.auth.IdentityAuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
public class MeController {
    private final IdentityAuthService identityAuthService;

    public MeController(IdentityAuthService identityAuthService) {
        this.identityAuthService = identityAuthService;
    }

    @GetMapping
    public AccountView me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return identityAuthService.currentAccount(authorization);
    }
}
