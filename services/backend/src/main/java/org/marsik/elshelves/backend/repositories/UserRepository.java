package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User getUserByEmail(String email);
    User getUserByVerificationCode(String verificationCode);

    User findByUuid(UUID uuid);
}
