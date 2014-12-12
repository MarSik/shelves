package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface UserRepository extends GraphRepository<User> {
    User getUserByUuid(UUID uuid);
    User getUserByEmail(String email);
    User getUserByVerificationCode(String verificationCode);
}
