package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.marsik.elshelves.api.entities.PartGroupApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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

	@OneToMany(mappedBy = "parent",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Set<Group> groups = new THashSet<>();

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Set<Type> types = new THashSet<>();

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    Set<NumericProperty> showProperties = new THashSet<>();

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
