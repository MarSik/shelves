package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.UserIdResolver;

import java.util.List;
import java.util.UUID;

/**
 * Represents an user of the system
 */
@Getter
@Setter
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
    SourceApiModel projectSource;

    @JsonIdentityReference(alwaysAsId = true)
    public List<AuthorizationApiModel> getAuthorizations() {
        return authorizations;
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
