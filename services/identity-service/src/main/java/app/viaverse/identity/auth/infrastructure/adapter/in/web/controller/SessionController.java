package app.viaverse.identity.auth.infrastructure.adapter.in.web.controller;

import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response.SessionView;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.mapper.SessionDtoMapper;
import app.viaverse.identity.auth.application.port.in.ListSessionsUseCase;
import app.viaverse.identity.auth.application.port.in.RevokeSessionUseCase;
import app.viaverse.identity.auth.domain.model.AuthSession;
import app.viaverse.identity.auth.infrastructure.security.JwtPrincipal;
import app.viaverse.identity.auth.infrastructure.security.JwtPrincipalResolver;
import app.viaverse.web.api.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/sessions")
@SecurityRequirement(name = "bearerAuth")
public class SessionController {

    private final ListSessionsUseCase listUseCase;
    private final RevokeSessionUseCase revokeUseCase;
    private final JwtPrincipalResolver jwtPrincipalResolver;
    private final SessionDtoMapper sessionDtoMapper;

    public SessionController(
            ListSessionsUseCase listUseCase,
            RevokeSessionUseCase revokeUseCase,
            JwtPrincipalResolver jwtPrincipalResolver,
            SessionDtoMapper sessionDtoMapper
    ) {
        this.listUseCase = listUseCase;
        this.revokeUseCase = revokeUseCase;
        this.jwtPrincipalResolver = jwtPrincipalResolver;
        this.sessionDtoMapper = sessionDtoMapper;
    }

    @GetMapping
    public ApiResponse<List<SessionView>> list(@AuthenticationPrincipal Jwt jwt) {
        JwtPrincipal principal = jwtPrincipalResolver.resolve(jwt);
        List<AuthSession> sessions = listUseCase.execute(new ListSessionsUseCase.Command(principal.accountId()));
        return ApiResponse.ok(sessionDtoMapper.toViews(sessions, principal.sessionId()));
    }

    @DeleteMapping("/{sessionId}")
    public ApiResponse<Void> revoke(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID sessionId) {
        JwtPrincipal principal = jwtPrincipalResolver.resolve(jwt);
        revokeUseCase.execute(new RevokeSessionUseCase.Command(
                principal.accountId(), sessionId, principal.sessionId(), false));
        return ApiResponse.ok(null);
    }

    @DeleteMapping
    public ApiResponse<Void> revokeAllExceptCurrent(@AuthenticationPrincipal Jwt jwt) {
        JwtPrincipal principal = jwtPrincipalResolver.resolve(jwt);
        revokeUseCase.execute(new RevokeSessionUseCase.Command(
                principal.accountId(), null, principal.sessionId(), true));
        return ApiResponse.ok(null);
    }
}

