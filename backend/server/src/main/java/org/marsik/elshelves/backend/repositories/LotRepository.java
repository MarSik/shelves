package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.repositories.results.LotMetric;
import org.springframework.data.jpa.repository.Query;

public interface LotRepository extends BaseOwnedEntityRepository<Lot> {
    @Query("SELECT NEW org.marsik.elshelves.backend.repositories.results.LotMetric(" +
            " count(l.id)," +
            " SUM(CASE l.used WHEN true THEN 1 ELSE 0 END)," +
            " SUM(CASE l.usedInPast WHEN true THEN 1 ELSE 0 END)" +
            " ) " +
            " FROM Lot l" +
            " WHERE l.valid = true")
    LotMetric lotCount();
}
