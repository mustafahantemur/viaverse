package app.viaverse.marketplace.marketplace.application.port.out;

import app.viaverse.marketplace.marketplace.domain.model.Job;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRepository {

    Job save(Job job);

    Optional<Job> findById(UUID jobId);

    List<Job> findAllByParticipantAccountId(UUID accountId);
}
