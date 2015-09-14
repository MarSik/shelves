package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.NamedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NamedEntityRepository extends JpaRepository<NamedEntity, UUID> {
    @Query("START user=node({0}) MATCH (e:NamedEntity) <-[:OWNS]- user WHERE e.name =~ {1} OR e.summary =~ {1} OR e.description =~ {1} RETURN DISTINCT e")
    Iterable<NamedEntity> queryByUser(User user, String query);
}
