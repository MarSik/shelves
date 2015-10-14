package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.User;

public interface UserRepository extends BaseIdentifiedEntityRepository<User> {
    User getUserByEmail(String email);
    User getUserByVerificationCode(String verificationCode);
}
