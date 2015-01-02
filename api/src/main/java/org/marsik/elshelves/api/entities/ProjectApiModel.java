package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.ember.Sideload;
import org.marsik.elshelves.api.entities.deserializers.PartTypeIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.RequirementIdDeserializer;

import java.util.Set;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@EmberModelName("project")
public class ProjectApiModel extends AbstractNamedEntityApiModel {
	String description;

	@Sideload(asType = RequirementApiModel.class)
	Set<RequirementApiModel> requires;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public Set<RequirementApiModel> getRequires() {
		return requires;
	}

	@JsonSetter
	@JsonDeserialize(contentUsing = RequirementIdDeserializer.class)
	public void setRequires(Set<RequirementApiModel> requires) {
		this.requires = requires;
	}
}
