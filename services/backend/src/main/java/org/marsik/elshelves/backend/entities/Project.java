package org.marsik.elshelves.backend.entities;

import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

@NodeEntity
public class Project extends NamedObject {
	String description;

	@RelatedTo(type = "REQUIRES")
	Set<Requirement> requires;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<Requirement> getRequires() {
		return requires;
	}

	public void setRequires(Set<Requirement> requires) {
		this.requires = requires;
	}
}
