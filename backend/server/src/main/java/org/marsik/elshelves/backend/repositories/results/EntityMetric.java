package org.marsik.elshelves.backend.repositories.results;

import lombok.Data;

@Data
public class EntityMetric {
    Long total;

    public EntityMetric(Long total) {
        this.total = total;
    }
}
