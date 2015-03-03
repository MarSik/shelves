package org.marsik.elshelves.backend.repositories;


import org.marsik.elshelves.backend.entities.Code;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface CodeRepository extends GraphRepository<Code> {
    Iterable<Code> findByOwner(User owner);
    Code findByUuid(UUID uuid);
    Code findByTypeAndCodeAndOwner(String type, String code, User owner);
}
