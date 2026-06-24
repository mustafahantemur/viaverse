package app.viaverse.mockwebbff.app.infrastructure.web;

import app.viaverse.mockwebbff.app.AppDtos.NotificationView;
import app.viaverse.mockwebbff.app.MockSessionService;
import app.viaverse.mockwebbff.shared.ApiResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app")
public class ActivityController {

    private final MockSessionService service;

    public ActivityController(MockSessionService service) {
        this.service = service;
    }

    @GetMapping("/notifications")
    ApiResponse<List<NotificationView>> notifications() {
        return ApiResponse.success(service.notifications());
    }
}
