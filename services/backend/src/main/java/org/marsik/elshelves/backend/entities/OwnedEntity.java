package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public abstract class OwnedEntity extends IdentifiedEntity {
	private static final Logger log = LoggerFactory.getLogger(OwnedEntity.class);

	@ManyToOne
	User owner;

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
				", id=" + id +
				'}';
	}

	@PrePersist
	void prePersist() {
		log.debug("Saving "+toString());
	}

	@Transient
	public boolean isNew() {
		return dbId == null;
	}
}
