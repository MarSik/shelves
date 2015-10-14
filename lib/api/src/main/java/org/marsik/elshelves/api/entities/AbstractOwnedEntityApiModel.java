package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
public abstract class AbstractOwnedEntityApiModel extends AbstractEntityApiModel {
    public AbstractOwnedEntityApiModel(UUID id) {
        super(id);
    }

    UserApiModel belongsTo;

    @JsonIdentityReference(alwaysAsId = true)
    public UserApiModel getBelongsTo() {
        return belongsTo;
    }
}
