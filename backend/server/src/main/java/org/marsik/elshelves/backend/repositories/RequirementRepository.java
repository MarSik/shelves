package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.User;

public interface RequirementRepository extends BaseIdentifiedEntityRepository<Requirement> {
	Iterable<Requirement> findByItemOwner(User owner);
}
