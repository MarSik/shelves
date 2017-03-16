package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Footprint;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FootprintRepository extends BaseOwnedEntityRepository<Footprint>, JpaSpecificationExecutor<Footprint> {
}
