package org.marsik.elshelves.backend.repositories.results;

import lombok.Data;

@Data
public class EntityCount {
    Long total;

    public EntityCount(Long total) {
        this.total = total;
    }
}
