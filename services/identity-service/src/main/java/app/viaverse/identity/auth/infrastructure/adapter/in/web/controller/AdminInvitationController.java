package app.viaverse.identity.auth.infrastructure.adapter.in.web.controller;

import app.viaverse.identity.auth.application.port.in.IssueAdminInvitationUseCase;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response.AdminInvitationResponse;
import app.viaverse.identity.auth.infrastructure.security.JwtPrincipal;
import app.viaverse.identity.auth.infrastructure.security.JwtPrincipalResolver;
import app.viaverse.web.api.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@SecurityRequirement(name = "bearerAuth")
public class AdminInvitationController {
    private final IssueAdminInvitationUseCase issueAdminInvitationUseCase;
    private final JwtPrincipalResolver jwtPrincipalResolver;

    public AdminInvitationController(
            IssueAdminInvitationUseCase issueAdminInvitationUseCase,
            JwtPrincipalResolver jwtPrincipalResolver
    ) {
        this.issueAdminInvitationUseCase = issueAdminInvitationUseCase;
        this.jwtPrincipalResolver = jwtPrincipalResolver;
    }

    @PostMapping("/invitations")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AdminInvitationResponse> issueInvitation(@AuthenticationPrincipal Jwt jwt) {
        JwtPrincipal principal = jwtPrincipalResolver.resolve(jwt);
        IssueAdminInvitationUseCase.Result result =
                issueAdminInvitationUseCase.execute(new IssueAdminInvitationUseCase.Command(principal.accountId()));
        return ApiResponse.ok(new AdminInvitationResponse(result.invitationToken(), result.expiresAt()));
    }
}

