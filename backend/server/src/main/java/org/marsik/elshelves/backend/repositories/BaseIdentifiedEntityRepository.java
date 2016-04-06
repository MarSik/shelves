package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.repositories.results.EntityCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface BaseIdentifiedEntityRepository<T> extends JpaRepository<T, Long> {
    T findById(UUID id);

    @Query("SELECT NEW org.marsik.elshelves.backend.repositories.results.EntityCount(count(*)) FROM IdentifiedEntity i")
    EntityCount totalCount();
}
