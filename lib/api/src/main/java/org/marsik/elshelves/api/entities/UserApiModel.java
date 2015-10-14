package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.UserIdResolver;

import java.util.List;
import java.util.UUID;

/**
 * Represents an user of the system
 */
@Data
@EqualsAndHashCode(of = {}, callSuper = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = UserIdResolver.class)
@EmberModelName("user")
public class UserApiModel extends AbstractEntityApiModel {
	public UserApiModel(UUID id) {
		super(id);
	}

	public UserApiModel() {
	}

	String name;
    String password;
    String email;

    List<AuthorizationApiModel> authorizations;

    @JsonIdentityReference(alwaysAsId = true)
    public List<AuthorizationApiModel> getAuthorizations() {
        return authorizations;
    }
}
