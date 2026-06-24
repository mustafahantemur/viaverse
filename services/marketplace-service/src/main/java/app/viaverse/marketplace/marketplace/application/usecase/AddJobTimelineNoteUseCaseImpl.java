package app.viaverse.marketplace.marketplace.application.usecase;

import app.viaverse.marketplace.marketplace.application.port.in.AddJobTimelineNoteUseCase;
import app.viaverse.marketplace.marketplace.application.port.out.JobRepository;
import app.viaverse.marketplace.marketplace.application.port.out.JobTimelineRepository;
import app.viaverse.marketplace.marketplace.domain.model.Job;
import app.viaverse.marketplace.marketplace.domain.model.JobTimelineEntry;
import app.viaverse.shared.kernel.error.ForbiddenException;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.shared.kernel.error.ValidationException;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddJobTimelineNoteUseCaseImpl implements AddJobTimelineNoteUseCase {

    private final JobRepository jobRepository;
    private final JobTimelineRepository timelineRepository;
    private final Clock clock;

    public AddJobTimelineNoteUseCaseImpl(
            JobRepository jobRepository,
            JobTimelineRepository timelineRepository,
            Clock clock
    ) {
        this.jobRepository = jobRepository;
        this.timelineRepository = timelineRepository;
        this.clock = clock;
    }

    @Override
    @ObservedAction("marketplace.job.timeline.note.add")
    @Transactional
    public JobTimelineEntry execute(Command command) {
        Job job = jobRepository.findById(command.jobId())
                .orElseThrow(() -> new NotFoundException("Job not found"));
        if (!job.hasParticipant(command.actorAccountId())) {
            throw new ForbiddenException("Only job participants can add timeline notes");
        }
        try {
            return timelineRepository.save(JobTimelineEntry.note(
                    command.jobId(),
                    command.actorAccountId(),
                    command.message(),
                    clock.instant()
            ));
        } catch (IllegalArgumentException exception) {
            throw new ValidationException(exception.getMessage(), Map.of("message", exception.getMessage()));
        }
    }
}
