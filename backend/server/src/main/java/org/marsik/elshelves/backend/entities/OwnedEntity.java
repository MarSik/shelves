package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public abstract class OwnedEntity extends IdentifiedEntity
		implements OwnedEntityInterface, UpdateableEntity {
	private static final Logger log = LoggerFactory.getLogger(OwnedEntity.class);

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@NotNull
	User owner;


	/**
	 * Barcode associated with this entity
	 */
	@OneToMany(mappedBy = "reference",
			fetch = FetchType.LAZY,
			orphanRemoval = true)
	Set<Code> codes = new THashSet<>();

	public void addCode(Code c) {
		c.setReference(this);
	}

	public void removeCode(Code c) {
		c.setReference(null);
	}

	public abstract boolean canBeDeleted();

	public abstract boolean canBeUpdated();

	@Override
	public String toString() {
		return getClass().getName() + "{" +
				"id=" + dbId +
				", uuid=" + id +
				'}';
	}

	@PrePersist
	void prePersist() {
		log.debug("Saving " + toString() + " ptr: " + String.valueOf(System.identityHashCode(this)));
	}

	@Override
	public void updateFrom(UpdateableEntity update) throws OperationNotPermitted {
		if (!(update instanceof OwnedEntity)) {
			throw new IllegalArgumentException();
		}

		update(update.getOwner(), this::setOwner);
		reconcileLists(((OwnedEntity) update).getCodes(), this::getCodes, this::addCode, this::removeCode);
	}

	@Override
	public void relink(Relinker relinker) {
		relinkItem(relinker, getOwner(), this::setOwner);
		relinkList(relinker, this::getCodes, this::addCode, this::removeCode);

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
	public Object shallowClone() {
		OwnedEntity entity = (OwnedEntity) super.shallowClone();
		entity.setCodes(new THashSet<>(this.getCodes()));
		return entity;
	}
}
