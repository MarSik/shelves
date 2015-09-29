package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;

@NoRepositoryBean
public interface BaseOwnedEntityRepository<T extends OwnedEntity> extends BaseIdentifiedEntityRepository<T> {
    Collection<T> findByOwner(User owner);
}
