package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@JsonSubTypes({
        @JsonSubTypes.Type(AbstractNamedEntityApiModel.class)
})
public abstract class AbstractOwnedEntityApiModel extends AbstractEntityApiModel {
    public AbstractOwnedEntityApiModel(UUID id) {
        super(id);
    }

    public AbstractOwnedEntityApiModel(String uuid) {
        super(uuid);
    }

    UserApiModel belongsTo;

    Set<CodeApiModel> codes;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
