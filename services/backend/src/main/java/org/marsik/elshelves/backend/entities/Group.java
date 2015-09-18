package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.marsik.elshelves.api.entities.PartGroupApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@Table(name = "groups")
@DefaultEmberModel(PartGroupApiModel.class)
public class Group extends NamedEntity {
	@ManyToOne
	Group parent;

	@OneToMany(mappedBy = "parent")
	Set<Group> groups;

	@ManyToMany
	Set<Type> types;

    @OneToMany
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
