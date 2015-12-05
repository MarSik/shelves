package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractOwnedEntityApiModel extends AbstractEntityApiModel {
    public AbstractOwnedEntityApiModel(UUID id) {
        super(id);
    }

    public AbstractOwnedEntityApiModel(String uuid) {
        super(uuid);
    }

    UserApiModel belongsTo;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
