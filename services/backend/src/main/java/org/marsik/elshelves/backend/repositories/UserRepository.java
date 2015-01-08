package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface UserRepository extends OwnedRepository<User> {
    User getUserByUuid(UUID uuid);
    User getUserByEmail(String email);
    User getUserByVerificationCode(String verificationCode);

	@Query("START u=node({0}) MATCH u -[:OWNS]-> (us:User) RETURN DISTINCT us")
	@Override
	Iterable<User> findByOwner(User owner);
}
