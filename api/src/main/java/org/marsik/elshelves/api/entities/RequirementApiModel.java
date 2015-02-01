package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.RequirementIdResolver;

import java.util.Set;
import java.util.UUID;

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

	public void setProject(ProjectApiModel project) {
		this.project = project;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public Set<PartTypeApiModel> getType() {
		return type;
	}

	@JsonSetter
	public void setType(Set<PartTypeApiModel> type) {
		this.type = type;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public Set<LotApiModel> getLots() {
		return lots;
	}

	@JsonSetter
	public void setLots(Set<LotApiModel> lots) {
		this.lots = lots;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
