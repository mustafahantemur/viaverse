package app.viaverse.mockwebbff.app.infrastructure.web;

import app.viaverse.mockwebbff.app.AppDtos.PatchProfileRequest;
import app.viaverse.mockwebbff.app.AppDtos.PatchSettingsRequest;
import app.viaverse.mockwebbff.app.AppDtos.ProfileView;
import app.viaverse.mockwebbff.app.AppDtos.SettingsView;
import app.viaverse.mockwebbff.app.MockProfileService;
import app.viaverse.mockwebbff.shared.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app")
public class ProfileSettingsController {

    private final MockProfileService service;

    public ProfileSettingsController(MockProfileService service) {
        this.service = service;
    }

    @GetMapping("/profile")
    ApiResponse<ProfileView> profile() {
        return ApiResponse.success(service.profile());
    }

    @PatchMapping("/profile")
    ApiResponse<ProfileView> patchProfile(@RequestBody PatchProfileRequest request) {
        return ApiResponse.success(service.patchProfile(request));
    }

    @GetMapping("/settings")
    ApiResponse<SettingsView> settings() {
        return ApiResponse.success(service.settings());
    }

    @PatchMapping("/settings")
    ApiResponse<SettingsView> patchSettings(@RequestBody PatchSettingsRequest request) {
        return ApiResponse.success(service.patchSettings(request));
    }
}
