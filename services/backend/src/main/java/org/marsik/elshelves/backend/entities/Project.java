package org.marsik.elshelves.backend.entities;

import org.marsik.elshelves.api.entities.ProjectApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

@NodeEntity
@DefaultEmberModel(ProjectApiModel.class)
public class Project extends NamedEntity implements StickerCapable {
	@RelatedTo(type = "REQUIRES")
	Set<Requirement> requires;

	public Set<Requirement> getRequires() {
		return requires;
	}

	public void setRequires(Set<Requirement> requires) {
		this.requires = requires;
	}

	public boolean canBeDeleted() {
		for (Requirement r: getRequires()) {
			if (!r.canBeDeleted()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getBaseUrl() {
		return "projects";
	}
}
