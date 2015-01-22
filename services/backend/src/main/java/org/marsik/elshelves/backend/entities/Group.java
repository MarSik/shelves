package org.marsik.elshelves.backend.entities;

import org.marsik.elshelves.api.entities.PartGroupApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

@NodeEntity
@DefaultEmberModel(PartGroupApiModel.class)
public class Group extends NamedEntity {
	@RelatedTo(type = "PARENT_OF", direction = Direction.INCOMING)
	Group parent;

	@RelatedTo(type = "PARENT_OF")
	Set<Group> groups;

	@RelatedTo(type = "CONTAINS")
	Set<Type> types;

    @RelatedTo(type = "SHOW_PROPERTY")
    Set<NumericProperty> showProperties;

	@PartOfUpdate
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

	@PartOfUpdate
	public Set<Type> getTypes() {
		return types;
	}

	public void setTypes(Set<Type> types) {
		this.types = types;
	}

	@Override
	public boolean canBeDeleted() {
		return true;
	}

    public Set<NumericProperty> getShowProperties() {
        return showProperties;
    }

    public void setShowProperties(Set<NumericProperty> showProperties) {
        this.showProperties = showProperties;
    }

    public Long getCount() {
		return Long.valueOf(getTypes().size());
	}

	public Long getNestedCount() {
		Long count = getCount();
		for (Group t: getGroups()) {
			count += t.getCount();
		}
		return count;
	}
}
