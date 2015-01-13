package org.marsik.elshelves.backend.entities;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

@NodeEntity
public class Group extends NamedEntity {
	@RelatedTo(type = "PARENT_OF", direction = Direction.INCOMING)
	Group parent;

	@RelatedTo(type = "PARENT_OF")
	Set<Group> groups;

	@RelatedTo(type = "CONTAINS")
	Set<Type> types;

	@PartOfUpdate
	public Group getParent() {
		return parent;
	}

	public void setParent(Group parent) {
		this.parent = parent;
	}

	@PartOfUpdate
	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	@PartOfUpdate
	public Set<Type> getTypes() {
		return types;
	}

	public void setTypes(Set<Type> types) {
		this.types = types;
	}
}
