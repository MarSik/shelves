package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.CodeIdResolver;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = CodeIdResolver.class)
@EmberModelName("code")
public class CodeApiModel extends AbstractEntityApiModel {
    public CodeApiModel(UUID id) {
        this.id = id;
    }

    public CodeApiModel() {
    }

    @NotNull
    String type; // CODE_TYPE - EAN, QR, UPC...
    @NotNull
    String code; // CODE_VALUE

    PolymorphicRecord reference;

    UserApiModel belongsTo;

    public PolymorphicRecord getReference() {
        return reference;
    }

    public void setReference(PolymorphicRecord reference) {
        this.reference = reference;
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

    @JsonIdentityReference(alwaysAsId = true)
    public UserApiModel getBelongsTo() {
        return belongsTo;
    }

    public void setBelongsTo(UserApiModel belongsTo) {
        this.belongsTo = belongsTo;
    }
}
