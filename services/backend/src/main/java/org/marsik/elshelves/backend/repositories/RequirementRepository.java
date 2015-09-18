package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RequirementRepository extends BaseOwnedEntityRepository<Requirement> {
	Iterable<Requirement> findByProjectOwner(User owner);
}
