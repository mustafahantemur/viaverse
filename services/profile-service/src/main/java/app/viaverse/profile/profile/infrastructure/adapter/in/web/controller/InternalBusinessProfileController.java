package app.viaverse.profile.profile.infrastructure.adapter.in.web.controller;

import app.viaverse.profile.config.ProfileInternalApiAuthorizer;
import app.viaverse.profile.profile.application.port.in.ApproveBusinessProfileUseCase;
import app.viaverse.profile.profile.application.port.in.ListSubmittedBusinessProfilesUseCase;
import app.viaverse.profile.profile.application.port.in.RejectBusinessProfileUseCase;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.request.RejectBusinessProfileRequest;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.response.BusinessProfileResponse;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.mapper.BusinessProfileDtoMapper;
import app.viaverse.web.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/business")
public class InternalBusinessProfileController {

    private static final String INTERNAL_TOKEN_HEADER = "X-Internal-Token";

    private final ListSubmittedBusinessProfilesUseCase listSubmittedBusinessProfilesUseCase;
    private final ApproveBusinessProfileUseCase approveBusinessProfileUseCase;
    private final RejectBusinessProfileUseCase rejectBusinessProfileUseCase;
    private final ProfileInternalApiAuthorizer internalApiAuthorizer;
    private final BusinessProfileDtoMapper mapper;

    public InternalBusinessProfileController(
            ListSubmittedBusinessProfilesUseCase listSubmittedBusinessProfilesUseCase,
            ApproveBusinessProfileUseCase approveBusinessProfileUseCase,
            RejectBusinessProfileUseCase rejectBusinessProfileUseCase,
            ProfileInternalApiAuthorizer internalApiAuthorizer,
            BusinessProfileDtoMapper mapper
    ) {
        this.listSubmittedBusinessProfilesUseCase = listSubmittedBusinessProfilesUseCase;
        this.approveBusinessProfileUseCase = approveBusinessProfileUseCase;
        this.rejectBusinessProfileUseCase = rejectBusinessProfileUseCase;
        this.internalApiAuthorizer = internalApiAuthorizer;
        this.mapper = mapper;
    }

    @GetMapping("/submissions")
    public ApiResponse<List<BusinessProfileResponse>> submissions(
            @RequestHeader(value = INTERNAL_TOKEN_HEADER, required = false) String internalToken
    ) {
        internalApiAuthorizer.requireAuthorized(internalToken);
        return ApiResponse.ok(listSubmittedBusinessProfilesUseCase.execute().stream()
                .map(mapper::toResponse)
                .toList());
    }

    @PostMapping("/{accountId}/approve")
    public ApiResponse<BusinessProfileResponse> approve(
            @PathVariable UUID accountId,
            @RequestHeader(value = INTERNAL_TOKEN_HEADER, required = false) String internalToken
    ) {
        internalApiAuthorizer.requireAuthorized(internalToken);
        return ApiResponse.ok(mapper.toResponse(approveBusinessProfileUseCase.execute(accountId)));
    }

    @PostMapping("/{accountId}/reject")
    public ApiResponse<BusinessProfileResponse> reject(
            @PathVariable UUID accountId,
            @Valid @RequestBody RejectBusinessProfileRequest request,
            @RequestHeader(value = INTERNAL_TOKEN_HEADER, required = false) String internalToken
    ) {
        internalApiAuthorizer.requireAuthorized(internalToken);
        return ApiResponse.ok(mapper.toResponse(rejectBusinessProfileUseCase.execute(
                new RejectBusinessProfileUseCase.Command(accountId, request.reason())
        )));
    }
}
