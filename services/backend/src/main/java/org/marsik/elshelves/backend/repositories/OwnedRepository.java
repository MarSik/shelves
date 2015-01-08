package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface OwnedRepository<T extends OwnedEntity> extends GraphRepository<T> {
	Iterable<T> findByOwner(User owner);
	T findByUuid(UUID uuid);
}
