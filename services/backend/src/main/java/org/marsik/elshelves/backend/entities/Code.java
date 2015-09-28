package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import org.marsik.elshelves.api.entities.CodeApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString(of = {}, callSuper = true)
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@DefaultEmberModel(CodeApiModel.class)
public class Code extends OwnedEntity {
    @NotNull
    @NotEmpty
    String type;

    @NotNull
    @NotEmpty
    String code;

    @ManyToOne
    NamedEntity reference;

    @Override
    public boolean canBeDeleted() {
        return true;
    }

    @Override
    public boolean canBeUpdated() {
        return false;
    }

    @PartOfUpdate
    public NamedEntity getReference() {
        return reference;
    }

}
