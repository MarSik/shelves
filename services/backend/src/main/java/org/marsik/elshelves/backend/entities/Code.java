package org.marsik.elshelves.backend.entities;

import org.hibernate.validator.constraints.NotEmpty;
import org.marsik.elshelves.api.entities.CodeApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.NotNull;
import java.util.Set;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @PartOfUpdate
    public NamedEntity getReference() {
        return reference;
    }

    public void setReference(NamedEntity reference) {
        this.reference = reference;
    }
}
