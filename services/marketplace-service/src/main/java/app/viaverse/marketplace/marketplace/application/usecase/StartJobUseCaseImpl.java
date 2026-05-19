package app.viaverse.marketplace.marketplace.application.usecase;

import app.viaverse.marketplace.marketplace.application.port.in.StartJobUseCase;
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
public class StartJobUseCaseImpl implements StartJobUseCase {

    private final JobRepository repository;
    private final JobTimelineRepository timelineRepository;
    private final MarketplaceEventPublisher eventPublisher;
    private final Clock clock;

    public StartJobUseCaseImpl(
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
    @ObservedAction("marketplace.job.start")
    @Transactional
    public Job execute(Command command) {
        Job current = repository.findById(command.jobId())
                .orElseThrow(() -> new NotFoundException("Job not found"));
        if (!current.getProviderAccountId().equals(command.providerAccountId())) {
            throw new ForbiddenException("Only the assigned provider can start the job");
        }
        try {
            var now = clock.instant();
            Job saved = repository.save(current.start(now));
            timelineRepository.save(JobTimelineEntry.system(saved.getId(), JobTimelineEventTypeEnum.JOB_STARTED, now));
            eventPublisher.publishJobStarted(saved);
            return saved;
        } catch (IllegalStateException exception) {
            throw new ConflictException(exception.getMessage());
        }
    }
}
