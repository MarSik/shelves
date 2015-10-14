package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Test, Long> {
}
