package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Footprint;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface FootprintRepository extends OwnedRepository<Footprint> {
}
