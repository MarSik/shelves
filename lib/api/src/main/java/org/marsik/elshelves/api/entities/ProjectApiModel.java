package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.ProjectIdResolver;

import java.util.Set;
import java.util.UUID;

/**
 * Deprecated
 * Used only for importing old backups
 */
@Data
@EqualsAndHashCode(of = {}, callSuper = true)
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
}
