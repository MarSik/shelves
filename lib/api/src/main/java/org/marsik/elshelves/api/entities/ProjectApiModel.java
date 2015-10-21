package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = ProjectIdResolver.class)
@EmberModelName("project")
public class ProjectApiModel extends AbstractNamedEntityApiModel {
	public ProjectApiModel(UUID id) {
		super(id);
	}

	public ProjectApiModel() {
	}

	String description;

	Set<RequirementApiModel> requirements;

	@JsonIdentityReference(alwaysAsId = true)
	public Set<RequirementApiModel> getRequirements() {
		return requirements;
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
