package org.marsik.elshelves.backend.repositories;

import java.util.List;

import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.api.dtos.LotMetric;
import org.marsik.elshelves.backend.repositories.results.MoneySum;
import org.marsik.elshelves.backend.repositories.results.UserMetric;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends BaseIdentifiedEntityRepository<User> {
    User getUserByEmail(String email);
    User getUserByVerificationCode(String verificationCode);
    User getUserByExternalIds(String externalId);

    @Query("SELECT NEW org.marsik.elshelves.backend.repositories.results.UserMetric(" +
            " COUNT(DISTINCT l.dbId)," +
            " SUM(CASE WHEN l.password IS NULL THEN 0 ELSE 1 END)," +
            " COUNT(ids)" +
            " ) " +
            " FROM User l LEFT JOIN l.externalIds ids")
    UserMetric userCount();

    @Query("SELECT NEW org.marsik.elshelves.api.dtos.LotMetric(" +
            " SUM(l.count)," +
            " SUM(CASE WHEN l.used = true THEN l.count ELSE 0 END)," +
            " SUM(CASE WHEN l.usedInPast = true THEN l.count ELSE 0 END)," +
            " SUM(CASE WHEN l.usedBy IS NULL THEN 0 ELSE l.count END)" +
            " ) " +
            " FROM Lot l LEFT JOIN l.usedBy r" +
            " WHERE l.valid = true AND l.owner = ?1")
    LotMetric lotCount(User user);

    @Query("SELECT NEW org.marsik.elshelves.backend.repositories.results.MoneySum("
            + " p.currencyPaid,"
            + " SUM(p.totalPricePaidRaw),"
            + " t.date,"
            + " t.created"
            + " ) "
            + " FROM Purchase p "
            + " JOIN p.transaction t"
            + " WHERE p.totalPricePaidRaw > 0"
            + "  AND p.owner = ?1"
            + " GROUP BY p.currencyPaid, t.date, t.created")
    List<MoneySum> totalPricePaid(User user);
}
