package org.marsik.elshelves.backend.entities;

import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

@NodeEntity
public class Project extends NamedEntity {
	@RelatedTo(type = "REQUIRES")
	Set<Requirement> requires;

	@PartOfUpdate
	public Set<Requirement> getRequires() {
		return requires;
	}

	public void setRequires(Set<Requirement> requires) {
		this.requires = requires;
	}
}
