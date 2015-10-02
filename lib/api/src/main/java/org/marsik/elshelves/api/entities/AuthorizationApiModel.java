package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.AuthorizationIdResolver;

import java.util.UUID;

@EmberModelName("authorization")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = AuthorizationIdResolver.class)
@Data
@EqualsAndHashCode(of = {}, callSuper = true)
public class AuthorizationApiModel extends AbstractEntityApiModel {
    public AuthorizationApiModel(UUID id) {
        super(id);
    }

    public AuthorizationApiModel() {
    }

    String name;
    String secret;
}
