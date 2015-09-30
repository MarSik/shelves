package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import org.marsik.elshelves.api.entities.CodeApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@ToString(of = {}, callSuper = true)
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

    @Override
    public boolean canBeDeleted() {
        return true;
    }

    @Override
    public boolean canBeUpdated() {
        return false;
    }

    @Override
    public void updateFrom(UpdateableEntity update0) {
        if (!(update0 instanceof Code)) {
            throw new IllegalArgumentException();
        }

        Code update = (Code) update0;

        update(update.getReference(), this::setReference);

        super.updateFrom(update);
    }
}
