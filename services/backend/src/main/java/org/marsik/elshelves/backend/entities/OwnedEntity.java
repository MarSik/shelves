package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
public abstract class OwnedEntity extends IdentifiedEntity
		implements OwnedEntityInterface, UpdateableEntity {
	private static final Logger log = LoggerFactory.getLogger(OwnedEntity.class);

	@ManyToOne(optional = false)
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
		log.debug("Saving "+toString());
	}

	@Override
	public void updateFrom(UpdateableEntity update) {
		if (!(update instanceof OwnedEntity)) {
			throw new IllegalArgumentException();
		}

		update(update.getOwner(), this::setOwner);
	}
}
