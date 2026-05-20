package app.viaverse.mockwebbff.app.infrastructure.web;

import app.viaverse.mockwebbff.app.AppDtos.BusinessView;
import app.viaverse.mockwebbff.app.AppDtos.CreateSavedSearchRequest;
import app.viaverse.mockwebbff.app.AppDtos.ProviderView;
import app.viaverse.mockwebbff.app.AppDtos.SavedSearchView;
import app.viaverse.mockwebbff.app.AppDtos.ServiceCategoryView;
import app.viaverse.mockwebbff.app.MockAppService;
import app.viaverse.mockwebbff.shared.ApiResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app")
public class ServiceDiscoveryController {

    private final MockAppService service;

    public ServiceDiscoveryController(MockAppService service) {
        this.service = service;
    }

    @GetMapping("/services/categories")
    ApiResponse<List<ServiceCategoryView>> categories() {
        return ApiResponse.success(service.categories());
    }

    @GetMapping("/providers")
    ApiResponse<List<ProviderView>> providers() {
        return ApiResponse.success(service.providers());
    }

    @GetMapping("/providers/{id}")
    ApiResponse<ProviderView> provider(@PathVariable String id) {
        return ApiResponse.success(service.provider(id));
    }

    @GetMapping("/businesses")
    ApiResponse<List<BusinessView>> businesses() {
        return ApiResponse.success(service.businesses());
    }

    @GetMapping("/businesses/{id}")
    ApiResponse<BusinessView> business(@PathVariable String id) {
        return ApiResponse.success(service.business(id));
    }

    @GetMapping("/searches/saved")
    ApiResponse<List<SavedSearchView>> savedSearches(@RequestParam(required = false) String surface) {
        return ApiResponse.success(service.savedSearches(surface));
    }

    @PostMapping("/searches/saved")
    ApiResponse<SavedSearchView> createSavedSearch(@RequestBody CreateSavedSearchRequest request) {
        return ApiResponse.success(service.createSavedSearch(request));
    }
}
