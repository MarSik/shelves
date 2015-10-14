package org.marsik.elshelves.backend.repositories;


import org.marsik.elshelves.backend.entities.Code;
import org.marsik.elshelves.backend.entities.User;

public interface CodeRepository extends BaseOwnedEntityRepository<Code> {
    Code findByTypeAndCodeAndOwner(String type, String code, User owner);
}
