package org.marsik.elshelves.backend.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.marsik.elshelves.backend.interfaces.Relinker;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class NumericPropertyValue implements RelinkableEntity {
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    NumericProperty property;

    @NotNull
    Long value;

    @Override
    public void relink(Relinker relinker) {
        setProperty((NumericProperty)relinker.findExisting(getProperty()));
        setEntity((NamedEntity)relinker.findExisting(getEntity()));
    }
}
