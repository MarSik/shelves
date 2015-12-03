package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.RequirementIdResolver;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id", resolver = RequirementIdResolver.class)
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

	Set<LotApiModel> lots;

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
