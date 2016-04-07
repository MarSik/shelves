package org.marsik.elshelves.backend.repositories.results;

import lombok.Data;

@Data
public class UserMetric {
    Long total;
    Long local;
    Long federated;

    public UserMetric(Long total, Long local, Long federated) {
        this.total = total;
        this.local = local;
        this.federated = federated;
    }
}
