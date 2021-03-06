package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.marsik.elshelves.backend.services.UuidGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class NamedEntity extends OwnedEntity
		implements UpdateableEntity, RevisionsSupport<TextRevision> {
	@NotEmpty
	@NotNull
	@Size(max = 255)
	String name;

	@Size(max = 255)
	String summary;

    Boolean flagged = false;

	@Lob
	String description;

	@ManyToMany(mappedBy = "describes", fetch = FetchType.LAZY)
	Set<Document> describedBy = new THashSet<>();

	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST },
			fetch = FetchType.LAZY)
	TextRevision previousRevision;

	public void addDescribedBy(Document d) {
		d.addDescribes(this);
	}

	public void removeDescribedBy(Document d) {
		d.removeDescribes(this);
	}

	@OneToMany(mappedBy = "entity",
			fetch = FetchType.LAZY,
			cascade = {CascadeType.ALL},
			orphanRemoval = true)
    Set<NumericPropertyValue> properties = new THashSet<>();

	public void addProperty(NumericPropertyValue v) {
		v.setEntity(this);
	}

	public void removeProperty(NumericPropertyValue v) {
		v.setEntity(null);
	}

	@Override
	public boolean canBeDeleted() {
		return false;
	}

	@Override
	public boolean canBeUpdated() {
		return true;
	}

	@Override
	public String toString() {
		return getClass().getName() + "{" +
				"id=" + dbId +
				", uuid=" + id +
				", name='" + name + '\'' +
				'}';
	}

	@Override
	public void updateFrom(UpdateableEntity update0) throws OperationNotPermitted {
		if (!(update0 instanceof NamedEntity)) {
			throw new IllegalArgumentException();
		}

		NamedEntity update = (NamedEntity)update0;

		update(update.getName(), this::setName);
		update(update.getSummary(), this::setSummary);
		update(update.getDescription(), this::setDescription);
		update(update.getFlagged(), this::setFlagged);

		reconcileLists(update.getDescribedBy(), this::getDescribedBy, this::addDescribedBy, this::removeDescribedBy);
		reconcileLists(update.getProperties(), this::getProperties, this::addProperty, this::removeProperty);

		super.updateFrom(update);
	}

	@Override
	public void relink(Relinker relinker) {
		relinkList(relinker, this::getDescribedBy, this::addDescribedBy, this::removeDescribedBy);
		relinkItem(relinker, getPreviousRevision(), this::setPreviousRevision);

		// Make a copy to prefent concurrent modification exception
		for (NumericPropertyValue value: new ArrayList<>(getProperties())) {
			value.relink(relinker);
		}

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

	@Override
	public boolean isRevisionNeeded(UpdateableEntity update0) {
		if (!(update0 instanceof NamedEntity)) {
			throw new IllegalArgumentException();
		}

		NamedEntity update = (NamedEntity)update0;

		return willUpdate(getName(), update.getName())
				|| willUpdate(getSummary(), update.getSummary())
				|| willUpdate(getDescription(), update.getDescription());
	}

	@Override
	public TextRevision createRevision(UpdateableEntity update, UuidGenerator uuidGenerator, User performedBy) {
		TextRevision revision = new TextRevision();
		revision.setId(uuidGenerator.generate());
		revision.setPerformedBy(performedBy);
		revision.setParent(getPreviousRevision());
		revision.setName(getName());
		revision.setSummary(getSummary());
		revision.setDescription(getDescription());

		return revision;
	}
}
