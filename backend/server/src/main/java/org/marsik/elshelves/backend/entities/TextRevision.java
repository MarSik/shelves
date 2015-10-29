package org.marsik.elshelves.backend.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class TextRevision extends IdentifiedEntity {
    String name;
    String summary;
    String description;

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    TextRevision parent;

    @ManyToOne
    @NotNull
    User performedBy;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
