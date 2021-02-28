package it.mulders.columbia.inventory;

import it.mulders.columbia.jobs.GlacierJobService;
import it.mulders.columbia.jobs.entity.InventoryRetrievalJobEntity;
import it.mulders.columbia.jobs.repository.InventoryRetrievalJobRepository;
import it.mulders.columbia.shared.TechnicalException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Slf4j
public class CheckInventoryJobStatusService {
    private final GlacierJobService glacierJobService;
    private final InventoryRetrievalJobRepository jobRepository;

    @Scheduled(fixedDelayString = "${columbia.job-check-interval}")
    public void checkAllJobsStatus() {
        jobRepository.findInProgress().forEach(this::checkJobStatus);
    }

    @SneakyThrows(TechnicalException.class)
    public void checkJobStatus(final InventoryRetrievalJobEntity jobEntity) {
        glacierJobService.getInventoryRetrievalJobStatus(jobEntity).ifPresent(status -> {
            switch (status) {
                case SUCCEEDED -> handleSucceededJob(jobEntity);
                case FAILED -> handleFailedJob(jobEntity);
                case IN_PROGRESS -> log.info("Job is still in progress: job-id={}", jobEntity.getId());
            }
        });
    }

    private void handleSucceededJob(final InventoryRetrievalJobEntity jobEntity) {
        log.info("Job has finished successfully: job-id={}", jobEntity.getId());
        try {
            var output = glacierJobService.getInventoryRetrievalJobOutput(jobEntity);
        } catch (TechnicalException e) {
            log.error("Could not retrieve job output: {}", e.getMessage());
        }
    }

    private void handleFailedJob(final InventoryRetrievalJobEntity jobEntity) {
        log.info("Job has failed: job-id={}", jobEntity.getId());
        var updated = jobEntity.toBuilder()
                .status(InventoryRetrievalJobEntity.Status.FAILED)
                .build();
        jobRepository.save(updated);
    }
}
