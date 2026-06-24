package app.viaverse.marketplace.marketplace.application.usecase;

import app.viaverse.marketplace.marketplace.application.port.in.ListCurrentJobsUseCase;
import app.viaverse.marketplace.marketplace.application.port.out.JobRepository;
import app.viaverse.marketplace.marketplace.domain.model.Job;
import app.viaverse.web.logging.ObservedAction;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ListCurrentJobsUseCaseImpl implements ListCurrentJobsUseCase {

    private final JobRepository repository;

    public ListCurrentJobsUseCaseImpl(JobRepository repository) {
        this.repository = repository;
    }

    @Override
    @ObservedAction("marketplace.job.list_current")
    public List<Job> execute(UUID accountId) {
        return repository.findAllByParticipantAccountId(accountId);
    }
}
