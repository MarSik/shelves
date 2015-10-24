package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.NamedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.Query;

public interface NamedEntityRepository extends BaseOwnedEntityRepository<NamedEntity> {
    @Query("SELECT e FROM NamedEntity e WHERE e.owner = ?1 AND (lower(e.name) like ?2 or lower(e.summary) like ?2 or lower(e.description) like ?2)")
    Iterable<NamedEntity> queryByOwnerAndText(User owner, String query);
}
