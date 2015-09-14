package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.marsik.elshelves.api.entities.CodeApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@NodeEntity
@DefaultEmberModel(CodeApiModel.class)
public class Code extends OwnedEntity {
    @NotNull
    @NotEmpty
    String type;

    @NotNull
    @NotEmpty
    String code;

    @RelatedTo(type = "IDENTIFIED_BY", direction = Direction.INCOMING)
    NamedEntity reference;

    @Override
    public boolean canBeDeleted() {
        return true;
    }

    @PartOfUpdate
    public NamedEntity getReference() {
        return reference;
    }

}
