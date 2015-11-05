package org.marsik.elshelves.backend.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Sku extends IdentifiedEntity {
    @NotNull
    @NotEmpty
    String sku;

    @ManyToOne(fetch = FetchType.LAZY)
    Source source;

    @ManyToOne(fetch = FetchType.LAZY)
    Type type;

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
