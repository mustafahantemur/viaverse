package app.viaverse.mockwebbff.app.infrastructure.web;

import app.viaverse.mockwebbff.app.AppDtos.SessionView;
import app.viaverse.mockwebbff.app.AppDtos.SwitchPersonaRequest;
import app.viaverse.mockwebbff.app.MockSessionService;
import app.viaverse.mockwebbff.shared.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app")
public class SessionController {

    private final MockSessionService service;

    public SessionController(MockSessionService service) {
        this.service = service;
    }

    @GetMapping("/me")
    ApiResponse<SessionView> me() {
        return ApiResponse.success(service.session());
    }

    @PostMapping("/session/persona")
    ApiResponse<SessionView> switchPersona(@RequestBody SwitchPersonaRequest request) {
        return ApiResponse.success(service.switchPersona(request));
    }

    @PostMapping("/dev/reset")
    ApiResponse<SessionView> reset() {
        return ApiResponse.success(service.reset());
    }
}
