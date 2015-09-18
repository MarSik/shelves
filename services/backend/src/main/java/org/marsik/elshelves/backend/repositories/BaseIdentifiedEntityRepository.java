package org.marsik.elshelves.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface BaseIdentifiedEntityRepository<T> extends JpaRepository<T, Long> {
    T findByUuid(UUID uuid);
}
