package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.marsik.elshelves.ember.EmberModelName;
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

	/**
	 * Deprecated
	 * Used only for reading old backups
	 */
	ProjectApiModel project;

	ItemApiModel item;

	Set<PartTypeApiModel> type;

	Long count;

	Set<PolymorphicRecord> lots;

	@JsonIdentityReference(alwaysAsId = true)
	public ProjectApiModel getProject() {
		return project;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public Set<PartTypeApiModel> getType() {
		return type;
	}

	public Set<PolymorphicRecord> getLots() {
		return lots;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public ItemApiModel getItem() {
		return item;
	}
}
