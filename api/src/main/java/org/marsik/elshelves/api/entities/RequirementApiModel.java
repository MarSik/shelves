package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.ember.Sideload;
import org.marsik.elshelves.api.entities.deserializers.PartTypeIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.ProjectIdDeserializer;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@EmberModelName("requirement")
public class RequirementApiModel extends AbstractEntityApiModel {
	@Sideload
	ProjectApiModel project;

	@Sideload
	PartTypeApiModel type;

	Long count;

	@JsonIdentityReference(alwaysAsId = true)
	public ProjectApiModel getProject() {
		return project;
	}

	@JsonDeserialize(using = ProjectIdDeserializer.class)
	public void setProject(ProjectApiModel project) {
		this.project = project;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public PartTypeApiModel getType() {
		return type;
	}

	@JsonSetter
	@JsonDeserialize(using = PartTypeIdDeserializer.class)
	public void setType(PartTypeApiModel type) {
		this.type = type;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}
