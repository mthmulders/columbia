package it.mulders.columbia.jobs.repository;

import it.mulders.columbia.jobs.entity.InventoryRetrievalJobEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface InventoryRetrievalJobRepository extends PagingAndSortingRepository<InventoryRetrievalJobEntity, UUID> {
    List<InventoryRetrievalJobEntity> findByStatus(final InventoryRetrievalJobEntity.Status status);

    default List<InventoryRetrievalJobEntity> findInProgress() {
        return findByStatus(InventoryRetrievalJobEntity.Status.IN_PROGRESS);
    }
}
