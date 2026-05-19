package app.viaverse.marketplace.marketplace.application.usecase;

import app.viaverse.marketplace.marketplace.application.port.in.ListJobTimelineUseCase;
import app.viaverse.marketplace.marketplace.application.port.out.JobRepository;
import app.viaverse.marketplace.marketplace.application.port.out.JobTimelineRepository;
import app.viaverse.marketplace.marketplace.domain.model.Job;
import app.viaverse.marketplace.marketplace.domain.model.JobTimelineEntry;
import app.viaverse.shared.kernel.error.ForbiddenException;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListJobTimelineUseCaseImpl implements ListJobTimelineUseCase {

    private final JobRepository jobRepository;
    private final JobTimelineRepository timelineRepository;

    public ListJobTimelineUseCaseImpl(
            JobRepository jobRepository,
            JobTimelineRepository timelineRepository
    ) {
        this.jobRepository = jobRepository;
        this.timelineRepository = timelineRepository;
    }

    @Override
    @ObservedAction("marketplace.job.timeline.list")
    @Transactional(readOnly = true)
    public List<JobTimelineEntry> execute(Command command) {
        Job job = jobRepository.findById(command.jobId())
                .orElseThrow(() -> new NotFoundException("Job not found"));
        if (!job.hasParticipant(command.actorAccountId())) {
            throw new ForbiddenException("Only job participants can read the timeline");
        }
        return timelineRepository.findAllByJobId(command.jobId());
    }
}
