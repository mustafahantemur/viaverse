package app.viaverse.marketplace.marketplace.application.usecase;

import app.viaverse.marketplace.marketplace.application.port.in.CompleteJobUseCase;
import app.viaverse.marketplace.marketplace.application.port.out.JobRepository;
import app.viaverse.marketplace.marketplace.application.port.out.MarketplaceEventPublisher;
import app.viaverse.marketplace.marketplace.domain.model.Job;
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
    private final MarketplaceEventPublisher eventPublisher;
    private final Clock clock;

    public CompleteJobUseCaseImpl(
            JobRepository repository,
            MarketplaceEventPublisher eventPublisher,
            Clock clock
    ) {
        this.repository = repository;
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
            Job saved = repository.save(current.complete(clock.instant()));
            eventPublisher.publishJobCompleted(saved);
            return saved;
        } catch (IllegalStateException exception) {
            throw new ConflictException(exception.getMessage());
        }
    }
}
