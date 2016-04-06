package org.marsik.elshelves.backend.repositories.results;

import lombok.Data;

@Data
public class LotCount {
    Long total;
    Long used;
    Long usedInPast;

    public LotCount(Long total, Long used, Long usedInPast) {
        this.total = total;
        this.used = used;
        this.usedInPast = usedInPast;
    }
}
