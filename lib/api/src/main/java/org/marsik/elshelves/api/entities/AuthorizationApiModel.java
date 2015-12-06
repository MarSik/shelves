package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.AuthorizationIdResolver;

import java.util.UUID;

@JsonTypeName("authorization")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id", resolver = AuthorizationIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "_type",
        visible = true,
        defaultImpl = AuthorizationApiModel.class)
@Getter
@Setter
public class AuthorizationApiModel extends AbstractEntityApiModel {
    public AuthorizationApiModel(UUID id) {
        super(id);
    }

    public AuthorizationApiModel() {
    }

    public AuthorizationApiModel(String uuid) {
        super(uuid);
    }

    String name;
    String secret;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
