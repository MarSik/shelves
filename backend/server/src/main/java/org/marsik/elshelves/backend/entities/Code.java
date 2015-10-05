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

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@DefaultEmberModel(CodeApiModel.class)
public class Code extends OwnedEntity implements UpdateableEntity {
    @NotNull
    @NotEmpty
    String type;

    @NotNull
    @NotEmpty
    String code;

    @ManyToOne(optional = false)
    NamedEntity reference;

    public void setReference(NamedEntity n) {
        if (reference != null) reference.getCodes().remove(this);
        reference = n;
        if (reference != null) reference.getCodes().add(this);
    }

    public void unsetReference(NamedEntity v) {
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
}
