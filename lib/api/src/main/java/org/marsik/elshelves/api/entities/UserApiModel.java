package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.UserIdResolver;

import java.util.List;
import java.util.UUID;

/**
 * Represents an user of the system
 */
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public List<AuthorizationApiModel> getAuthorizations() {
        return authorizations;
    }

    public void setAuthorizations(List<AuthorizationApiModel> authorizations) {
        this.authorizations = authorizations;
    }
}
