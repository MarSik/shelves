package org.marsik.elshelves.backend.entities;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;
import java.util.UUID;

@NodeEntity
public class Group extends NamedObject {
	@RelatedTo(type = "PARENT_OF", direction = Direction.INCOMING)
	Group parent;

	@RelatedTo(type = "PARENT_OF")
	Set<Group> groups;

	@RelatedTo(type = "CONTAINS")
	Set<Type> types;

	public Group getParent() {
		return parent;
	}

	public void setParent(Group parent) {
		this.parent = parent;
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	public Set<Type> getTypes() {
		return types;
	}

	public void setTypes(Set<Type> types) {
		this.types = types;
	}
}
