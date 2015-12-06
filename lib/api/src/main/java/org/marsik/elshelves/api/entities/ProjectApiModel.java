package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.ProjectIdResolver;

import java.util.Set;
import java.util.UUID;

/**
 * Deprecated
 * Used only for importing old backups
 */
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id", resolver = ProjectIdResolver.class)
@JsonTypeName("project")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "_type",
		visible = true,
		defaultImpl = ProjectApiModel.class)
public class ProjectApiModel extends AbstractNamedEntityApiModel {
	public ProjectApiModel(UUID id) {
		super(id);
	}

	public ProjectApiModel() {
	}

	public ProjectApiModel(String uuid) {
		super(uuid);
	}

	String description;

	Set<RequirementApiModel> requirements;


	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
