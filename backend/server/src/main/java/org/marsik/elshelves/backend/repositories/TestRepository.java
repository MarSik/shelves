package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface TestRepository extends JpaRepository<Test, Long>, QueryDslPredicateExecutor<Test> {
}
