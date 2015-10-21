package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.CodeIdResolver;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = CodeIdResolver.class)
@EmberModelName("code")
public class CodeApiModel extends AbstractEntityApiModel {
    public CodeApiModel(UUID id) {
        super(id);
    }

    public CodeApiModel() {
    }

    @NotNull
    String type; // CODE_TYPE - EAN, QR, UPC...
    @NotNull
    String code; // CODE_VALUE

    PolymorphicRecord reference;

    UserApiModel belongsTo;

    @JsonIdentityReference(alwaysAsId = true)
    public UserApiModel getBelongsTo() {
        return belongsTo;
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
