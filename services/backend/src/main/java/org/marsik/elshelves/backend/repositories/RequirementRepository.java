package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RequirementRepository extends JpaRepository<Requirement, UUID> {
	//@Query("START u=node({0}) MATCH u -[:OWNS]-> (p:Project) -- (r:Requirement) RETURN DISTINCT r")
	Iterable<Requirement> findByProjectOwner(User owner);
    Requirement findByUuid(UUID uuid);
}
