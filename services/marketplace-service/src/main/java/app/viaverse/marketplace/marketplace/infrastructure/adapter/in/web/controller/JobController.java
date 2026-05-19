package app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.controller;

import app.viaverse.marketplace.marketplace.application.port.in.AddJobTimelineNoteUseCase;
import app.viaverse.marketplace.marketplace.application.port.in.CompleteJobUseCase;
import app.viaverse.marketplace.marketplace.application.port.in.ListJobTimelineUseCase;
import app.viaverse.marketplace.marketplace.application.port.in.ListCurrentJobsUseCase;
import app.viaverse.marketplace.marketplace.application.port.in.StartJobUseCase;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.dto.request.AddJobTimelineNoteRequest;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.dto.response.JobResponse;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.dto.response.JobTimelineEntryResponse;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.mapper.MarketplaceDtoMapper;
import app.viaverse.web.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    private final ListCurrentJobsUseCase listCurrentJobsUseCase;
    private final StartJobUseCase startJobUseCase;
    private final CompleteJobUseCase completeJobUseCase;
    private final ListJobTimelineUseCase listJobTimelineUseCase;
    private final AddJobTimelineNoteUseCase addJobTimelineNoteUseCase;
    private final MarketplaceDtoMapper mapper;

    public JobController(
            ListCurrentJobsUseCase listCurrentJobsUseCase,
            StartJobUseCase startJobUseCase,
            CompleteJobUseCase completeJobUseCase,
            ListJobTimelineUseCase listJobTimelineUseCase,
            AddJobTimelineNoteUseCase addJobTimelineNoteUseCase,
            MarketplaceDtoMapper mapper
    ) {
        this.listCurrentJobsUseCase = listCurrentJobsUseCase;
        this.startJobUseCase = startJobUseCase;
        this.completeJobUseCase = completeJobUseCase;
        this.listJobTimelineUseCase = listJobTimelineUseCase;
        this.addJobTimelineNoteUseCase = addJobTimelineNoteUseCase;
        this.mapper = mapper;
    }

    @GetMapping("/me/jobs")
    public ApiResponse<List<JobResponse>> mine(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.ok(listCurrentJobsUseCase.execute(accountId(jwt)).stream()
                .map(mapper::toResponse)
                .toList());
    }

    @PostMapping("/jobs/{jobId}/start")
    public ApiResponse<JobResponse> start(
            @PathVariable UUID jobId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ApiResponse.ok(mapper.toResponse(startJobUseCase.execute(
                new StartJobUseCase.Command(jobId, accountId(jwt))
        )));
    }

    @PostMapping("/jobs/{jobId}/complete")
    public ApiResponse<JobResponse> complete(
            @PathVariable UUID jobId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ApiResponse.ok(mapper.toResponse(completeJobUseCase.execute(
                new CompleteJobUseCase.Command(jobId, accountId(jwt))
        )));
    }

    @GetMapping("/jobs/{jobId}/timeline")
    public ApiResponse<List<JobTimelineEntryResponse>> timeline(
            @PathVariable UUID jobId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ApiResponse.ok(listJobTimelineUseCase.execute(
                        new ListJobTimelineUseCase.Command(jobId, accountId(jwt))
                ).stream()
                .map(mapper::toResponse)
                .toList());
    }

    @PostMapping("/jobs/{jobId}/timeline/notes")
    public ApiResponse<JobTimelineEntryResponse> addNote(
            @PathVariable UUID jobId,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @org.springframework.web.bind.annotation.RequestBody AddJobTimelineNoteRequest request
    ) {
        return ApiResponse.ok(mapper.toResponse(addJobTimelineNoteUseCase.execute(
                new AddJobTimelineNoteUseCase.Command(jobId, accountId(jwt), request.message())
        )));
    }

    private UUID accountId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
