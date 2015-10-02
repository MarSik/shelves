package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.marsik.elshelves.api.entities.PartGroupApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString(of = {}, callSuper = true)
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@Table(name = "groups")
@DefaultEmberModel(PartGroupApiModel.class)
public class Group extends NamedEntity {
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Group parent;

	public void setParent(Group b) {
		if (parent != null) parent.getGroups().remove(this);
		parent = b;
		if (parent != null) parent.getGroups().add(this);
	}

	@OneToMany(mappedBy = "parent",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Set<Group> groups = new THashSet<>();

	public void addGroup(Group b) {
		b.setParent(this);
	}

	public void removeGroup(Group b) {
		b.setParent(null);
	}

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Set<Type> types = new THashSet<>();

	public void addType(Type t) {
		types.add(t);
		t.getGroups().add(this);
	}

	public void removeType(Type t) {
		types.remove(t);
		t.getGroups().remove(this);
	}

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    Set<NumericProperty> showProperties = new THashSet<>();

	public void addShowProperty(NumericProperty p) {
		showProperties.add(p);
	}

	public void removeShowProperty(NumericProperty p) {
		showProperties.remove(p);
	}

	@Override
	public boolean canBeDeleted() {
		return true;
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

	@Override
	public void updateFrom(UpdateableEntity update0) {
		if (!(update0 instanceof Group)) {
			throw new IllegalArgumentException();
		}

		Group update = (Group)update0;

		update(update.getParent(), this::setParent);
		update(update.getShowProperties(), this::setShowProperties);

		reconcileLists(this, update, Group::getTypes, Type::addGroup, Type::removeGroup);
		reconcileLists(this, update, Group::getGroups, Group::addGroup, Group::removeGroup);

		super.updateFrom(update0);
	}
}
