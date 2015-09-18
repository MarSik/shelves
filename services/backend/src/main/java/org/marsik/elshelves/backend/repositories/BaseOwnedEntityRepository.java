package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface BaseOwnedEntityRepository<T extends OwnedEntity> extends BaseIdentifiedEntityRepository<T> {
    Iterable<T> findByOwner(User owner);
}
