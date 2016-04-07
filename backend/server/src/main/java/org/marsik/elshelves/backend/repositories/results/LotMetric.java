package org.marsik.elshelves.backend.repositories.results;

import lombok.Data;

@Data
public class LotMetric {
    Long total;
    Long used;
    Long usedInPast;

    public LotMetric(Long total, Long used, Long usedInPast) {
        this.total = total;
        this.used = used;
        this.usedInPast = usedInPast;
    }
}
