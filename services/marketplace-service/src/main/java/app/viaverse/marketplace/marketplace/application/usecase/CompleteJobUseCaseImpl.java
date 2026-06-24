package app.viaverse.marketplace.marketplace.application.usecase;

import app.viaverse.marketplace.marketplace.application.port.in.CompleteJobUseCase;
import app.viaverse.marketplace.marketplace.application.port.out.JobRepository;
import app.viaverse.marketplace.marketplace.application.port.out.JobTimelineRepository;
import app.viaverse.marketplace.marketplace.application.port.out.MarketplaceEventPublisher;
import app.viaverse.marketplace.marketplace.domain.enums.JobTimelineEventTypeEnum;
import app.viaverse.marketplace.marketplace.domain.model.Job;
import app.viaverse.marketplace.marketplace.domain.model.JobTimelineEntry;
import app.viaverse.shared.kernel.error.ConflictException;
import app.viaverse.shared.kernel.error.ForbiddenException;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompleteJobUseCaseImpl implements CompleteJobUseCase {

    private final JobRepository repository;
    private final JobTimelineRepository timelineRepository;
    private final MarketplaceEventPublisher eventPublisher;
    private final Clock clock;

    public CompleteJobUseCaseImpl(
            JobRepository repository,
            JobTimelineRepository timelineRepository,
            MarketplaceEventPublisher eventPublisher,
            Clock clock
    ) {
        this.repository = repository;
        this.timelineRepository = timelineRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Override
    @ObservedAction("marketplace.job.complete")
    @Transactional
    public Job execute(Command command) {
        Job current = repository.findById(command.jobId())
                .orElseThrow(() -> new NotFoundException("Job not found"));
        if (!current.getRequesterAccountId().equals(command.requesterAccountId())) {
            throw new ForbiddenException("Only the requester can complete the job");
        }
        try {
            var now = clock.instant();
            Job saved = repository.save(current.complete(now));
            timelineRepository.save(JobTimelineEntry.system(saved.getId(), JobTimelineEventTypeEnum.JOB_COMPLETED, now));
            eventPublisher.publishJobCompleted(saved);
            return saved;
        } catch (IllegalStateException exception) {
            throw new ConflictException(exception.getMessage());
        }
    }
}
