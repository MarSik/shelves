package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.ProjectIdResolver;

import java.util.Set;
import java.util.UUID;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public Set<RequirementApiModel> getRequirements() {
		return requirements;
	}

	@JsonSetter
	public void setRequirements(Set<RequirementApiModel> requirements) {
		this.requirements = requirements;
	}
}
