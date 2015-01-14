package org.marsik.elshelves.backend.entities;

import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

@NodeEntity
public class Project extends NamedEntity {
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
}
