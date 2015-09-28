package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseOwnedEntityRepository<T extends OwnedEntity> extends BaseIdentifiedEntityRepository<T> {
    Iterable<T> findByOwner(User owner);
}
