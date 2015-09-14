package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.marsik.elshelves.api.entities.PartGroupApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
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

	@PartOfUpdate
	public Set<Type> getTypes() {
		return types;
	}

	@Override
	public boolean canBeDeleted() {
		return true;
	}

    @PartOfUpdate
    public Set<NumericProperty> getShowProperties() {
        return showProperties;
    }

    public Long getCount() {
		return (long) getTypes().size();
	}

	public Long getNestedCount() {
		Long count = getCount();
		for (Group t: getGroups()) {
			count += t.getCount();
		}
		return count;
	}
}
