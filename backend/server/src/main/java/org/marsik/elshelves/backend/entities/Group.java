package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.marsik.elshelves.api.entities.PartGroupApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.interfaces.Relinker;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "groups")
@DefaultEmberModel(PartGroupApiModel.class)
public class Group extends NamedEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	Group parent;

	public void setParent(Group b) {
		if (parent != null) parent.getGroups().remove(this);
		parent = b;
		if (parent != null) parent.getGroups().add(this);
	}

	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
	Set<Group> groups = new THashSet<>();

	public void addGroup(Group b) {
		b.setParent(this);
	}

	public void removeGroup(Group b) {
		b.setParent(null);
	}

	@ManyToMany(fetch = FetchType.LAZY)
	Set<Type> types = new THashSet<>();

	public void addType(Type t) {
		types.add(t);
		t.getGroups().add(this);
	}

	public void removeType(Type t) {
		types.remove(t);
		t.getGroups().remove(this);
	}

    @ManyToMany(fetch = FetchType.LAZY)
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
	public void updateFrom(UpdateableEntity update0) throws OperationNotPermitted {
		if (!(update0 instanceof Group)) {
			throw new IllegalArgumentException();
		}

		Group update = (Group)update0;

		update(update.getParent(), this::setParent);
		update(update.getShowProperties(), this::setShowProperties);

		reconcileLists(update.getTypes(), this::getTypes, this::addType, this::removeType);
		reconcileLists(update.getGroups(), this::getGroups, this::addGroup, this::removeGroup);

		super.updateFrom(update0);
	}

	@Override
	public void relink(Relinker relinker) {

		relinkItem(relinker, getParent(), this::setParent);
		relinkList(relinker, this::getTypes, this::addType, this::removeType);
		relinkList(relinker, this::getGroups, this::addGroup, this::removeGroup);
		relinkList(relinker, this::getShowProperties, this::addShowProperty, this::removeShowProperty);

		super.relink(relinker);
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
