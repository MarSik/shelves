package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
public abstract class OwnedEntity extends IdentifiedEntity implements OwnedEntityInterface {
	private static final Logger log = LoggerFactory.getLogger(OwnedEntity.class);

	@ManyToOne
	User owner;

	@Override
	@PartOfUpdate
	public User getOwner() {
		return owner;
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
		log.debug("Saving "+toString());
	}
}
