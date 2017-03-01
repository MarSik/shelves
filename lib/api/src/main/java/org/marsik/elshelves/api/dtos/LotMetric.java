package org.marsik.elshelves.api.dtos;

import lombok.Data;

@Data
public class LotMetric {
    Long total;
    Long used;
    Long usedInPast;
    Long assigned;

    public LotMetric(Long total, Long used, Long usedInPast, Long assigned) {
        this.total = total;
        this.used = used;
        this.usedInPast = usedInPast;
        this.assigned = assigned;
    }
}
