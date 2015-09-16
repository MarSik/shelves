package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

public interface OwnedEntityRepository extends JpaRepository<OwnedEntity, UUID> {
    Iterable<OwnedEntity> findByOwner(User owner);
    OwnedEntity findByUuid(UUID uuid);
}
