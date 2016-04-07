package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.results.UserMetric;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends BaseIdentifiedEntityRepository<User> {
    User getUserByEmail(String email);
    User getUserByVerificationCode(String verificationCode);
    User getUserByExternalIds(String externalId);

    @Query("SELECT NEW org.marsik.elshelves.backend.repositories.results.UserMetric(" +
            " COUNT(DISTINCT l.dbId)," +
            " SUM(CASE l.password WHEN NULL THEN 0 ELSE 1 END)," +
            " COUNT(ids)" +
            " ) " +
            " FROM User l LEFT JOIN l.externalIds ids")
    UserMetric userCount();
}
