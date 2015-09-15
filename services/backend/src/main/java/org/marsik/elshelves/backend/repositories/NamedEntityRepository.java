package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.NamedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NamedEntityRepository extends JpaRepository<NamedEntity, UUID> {
    // TODO match on summary and description as well
    Iterable<NamedEntity> findDistinctByOwnerAndNameLikeAllIgnoreCase(User owner,
            String query);
}
