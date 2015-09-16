package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.NamedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface NamedEntityRepository extends JpaRepository<NamedEntity, UUID> {
    @Query("SELECT e FROM NamedEntity e WHERE e.owner = ?1 AND (lower(e.name) like ?2 or lower(e.summary) like ?1 or lower(e.description) like ?1)")
    Iterable<NamedEntity> queryByOwnerAndText(User owner, String query);
}
