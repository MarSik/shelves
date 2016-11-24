package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.repositories.results.EntityMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface BaseIdentifiedEntityRepository<T> extends JpaRepository<T, Long>, QueryDslPredicateExecutor<T> {
    T findById(UUID id);

    @Query("SELECT NEW org.marsik.elshelves.backend.repositories.results.EntityMetric(count(*)) FROM IdentifiedEntity i")
    EntityMetric totalCount();
}
