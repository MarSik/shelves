package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.RequirementIdResolver;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = {}, callSuper = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = RequirementIdResolver.class)
@EmberModelName("requirement")
public class RequirementApiModel extends AbstractEntityApiModel {
	public RequirementApiModel(UUID id) {
		super(id);
	}

	public RequirementApiModel() {
	}

    String name;
    String summary;

	ProjectApiModel project;

	Set<PartTypeApiModel> type;

	Long count;

	Set<LotApiModel> lots;

	@JsonIdentityReference(alwaysAsId = true)
	public ProjectApiModel getProject() {
		return project;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public Set<PartTypeApiModel> getType() {
		return type;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public Set<LotApiModel> getLots() {
		return lots;
	}
}
