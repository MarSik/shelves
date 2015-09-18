package org.marsik.elshelves.backend.repositories;


import org.marsik.elshelves.backend.entities.Code;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CodeRepository extends BaseOwnedEntityRepository<Code> {
    Code findByTypeAndCodeAndOwner(String type, String code, User owner);
}
