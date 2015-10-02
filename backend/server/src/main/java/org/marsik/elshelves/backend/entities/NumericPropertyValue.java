package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Entity
public class NumericPropertyValue {
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE },
            optional = false)
    NamedEntity entity;

    public void setEntity(NamedEntity v) {
        if (entity != null) entity.getProperties().remove(this);
        entity = v;
        if (entity != null) entity.getProperties().add(this);
    }

    public void unsetEntity(NamedEntity v) {
        assert  v.equals(entity);
        setEntity(null);
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE },
            optional = false)
    NumericProperty property;

    @NotNull
    Long value;
}
