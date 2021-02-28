package it.mulders.columbia.inventory;

import it.mulders.columbia.jobs.GlacierJobService;
import it.mulders.columbia.jobs.entity.InventoryRetrievalJobEntity;
import it.mulders.columbia.jobs.repository.InventoryRetrievalJobRepository;
import it.mulders.columbia.shared.TechnicalException;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static it.mulders.columbia.jobs.entity.InventoryRetrievalJobEntity.Status.FAILED;
import static it.mulders.columbia.jobs.entity.InventoryRetrievalJobEntity.Status.IN_PROGRESS;
import static it.mulders.columbia.jobs.entity.InventoryRetrievalJobEntity.Status.SUCCEEDED;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CheckInventoryJobStatusServiceTest implements WithAssertions {
    private final GlacierJobService glacierJobService = mock(GlacierJobService.class);
    private final InventoryRetrievalJobRepository jobRepository = mock(InventoryRetrievalJobRepository.class);

    private final CheckInventoryJobStatusService service = new CheckInventoryJobStatusService(
            glacierJobService,
            jobRepository
    );

    @Test
    void should_fetch_job_status_for_pending_job() throws TechnicalException {
        // Arrange
        var jobId = "42";
        var job = InventoryRetrievalJobEntity.builder()
                .jobId(jobId)
                .vaultName("Example")
                .build();
        when(jobRepository.findInProgress()).thenReturn(List.of(job));

        // Act
        service.checkAllJobsStatus();

        // Assert
        verify(glacierJobService).getInventoryRetrievalJobStatus(job);
    }

    @DisplayName("For failed jobs")
    @Nested
    class FailedJob {
        @Test
        // Might later add "completed" timestamp to job entity in DB
        void should_update_job() throws TechnicalException {
            // Arrange
            var job = InventoryRetrievalJobEntity.builder()
                    .build();
            when(jobRepository.findInProgress()).thenReturn(List.of(job));
            when(glacierJobService.getInventoryRetrievalJobStatus(any()))
                    .thenReturn(Optional.of(FAILED));

            // Act
            service.checkAllJobsStatus();

            // Assert
            var captor = ArgumentCaptor.forClass(InventoryRetrievalJobEntity.class);
            verify(jobRepository).save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(FAILED);
        }
    }

    @DisplayName("For jobs in progress")
    @Nested
    class InProgressJob {
        @Test
        // Might later add "last checked" timestamp to job entity in DB
        void should_not_update_job() throws TechnicalException {
            // Arrange
            var job = InventoryRetrievalJobEntity.builder()
                    .build();
            when(jobRepository.findInProgress()).thenReturn(List.of(job));
            when(glacierJobService.getInventoryRetrievalJobStatus(any()))
                    .thenReturn(Optional.of(IN_PROGRESS));

            // Act
            service.checkAllJobsStatus();

            // Assert
            verify(jobRepository, never()).save(any());
        }
    }

    @DisplayName("For succeeded jobs")
    @Nested
    class SucceededJob {
        @Test
        void should_fetch_job_output() throws TechnicalException {
            // Arrange
            var jobId = RandomStringUtils.randomAlphanumeric(16);
            var job = InventoryRetrievalJobEntity.builder()
                    .jobId(jobId)
                    .build();
            when(jobRepository.findInProgress()).thenReturn(List.of(job));
            when(glacierJobService.getInventoryRetrievalJobStatus(any()))
                    .thenReturn(Optional.of(SUCCEEDED));

            // Act
            service.checkAllJobsStatus();

            // Assert
            verify(glacierJobService).getInventoryRetrievalJobOutput(job);
        }
    }
}