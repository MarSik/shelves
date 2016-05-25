package org.marsik.elshelves.backend.repositories;

import java.util.List;

import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.repositories.results.LotMetric;
import org.marsik.elshelves.backend.repositories.results.MoneySum;
import org.springframework.data.jpa.repository.Query;

public interface LotRepository extends BaseOwnedEntityRepository<Lot> {
    @Query("SELECT NEW org.marsik.elshelves.backend.repositories.results.LotMetric(" +
            " SUM(l.count)," +
            " SUM(CASE l.used WHEN true THEN l.count ELSE 0 END)," +
            " SUM(CASE l.usedInPast WHEN true THEN l.count ELSE 0 END)" +
            " ) " +
            " FROM Lot l" +
            " WHERE l.valid = true")
    LotMetric lotCount();

    @Query("SELECT NEW org.marsik.elshelves.backend.repositories.results.MoneySum("
            + " p.currencyPaid,"
            + " SUM(p.totalPricePaidRaw),"
            + " t.date"
            + " ) "
            + " FROM PurchasedLot l"
            + " JOIN l.purchase p "
            + " JOIN p.transaction t"
            + " WHERE p.totalPricePaidRaw > 0"
            + " GROUP BY p.currencyPaid, t.date")
    List<MoneySum> totalPricePaid();
}
