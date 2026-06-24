package app.viaverse.marketplace.marketplace.application.port.out;

import app.viaverse.marketplace.marketplace.domain.model.JobTimelineEntry;
import java.util.List;
import java.util.UUID;

public interface JobTimelineRepository {

    JobTimelineEntry save(JobTimelineEntry entry);

    List<JobTimelineEntry> findAllByJobId(UUID jobId);
}
