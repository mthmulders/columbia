package it.mulders.columbia.jobs.repository;

import it.mulders.columbia.jobs.entity.InventoryRetrievalJobEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface InventoryRetrievalJobRepository extends CrudRepository<InventoryRetrievalJobEntity, UUID> {
}
