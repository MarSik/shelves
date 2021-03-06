package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import org.marsik.elshelves.api.entities.CodeApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.interfaces.Relinker;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DefaultEmberModel(CodeApiModel.class)
public class Code extends OwnedEntity implements UpdateableEntity {
    @NotNull
    @NotEmpty
    String type;

    @NotNull
    @NotEmpty
    String code;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @NotNull
    OwnedEntity reference;

    public void setReference(OwnedEntity n) {
        if (reference != null) reference.getCodes().remove(this);
        reference = n;
        if (reference != null) reference.getCodes().add(this);
    }

    public void unsetReference(OwnedEntity v) {
        assert v.equals(reference);
        setReference(null);
    }

    @Override
    public boolean canBeDeleted() {
        return true;
    }

    @Override
    public boolean canBeUpdated() {
        return false;
    }

    @Override
    public void relink(Relinker relinker) {
        relinkItem(relinker, getReference(), this::setReference);
        super.relink(relinker);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
