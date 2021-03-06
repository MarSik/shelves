package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.UserIdResolver;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

/**
 * Represents an user of the system
 */
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id", resolver = UserIdResolver.class)
@JsonTypeName("user")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "_type",
        visible = true,
        defaultImpl = UserApiModel.class)
public class UserApiModel extends AbstractEntityApiModel {
	public UserApiModel(UUID id) {
		super(id);
	}

	public UserApiModel() {
	}

    public UserApiModel(String uuid) {
        super(uuid);
    }

    String name;
    String password;
    String email;

    @Size(min = 3, max = 3)
    String currency;

    List<AuthorizationApiModel> authorizations;
    PartGroupApiModel lostAndFound;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
