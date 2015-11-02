package org.marsik.elshelves.backend.entities;

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
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;

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
	}

	@Override
	public void relink(Relinker relinker) {
		relinkItem(relinker, getOwner(), this::setOwner);

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
