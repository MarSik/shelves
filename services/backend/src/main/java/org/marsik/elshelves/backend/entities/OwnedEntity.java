package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"uuid"})
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class OwnedEntity {
	@ManyToOne
	@NotNull
	User owner;

	@Id
    Long id;

	UUID uuid;

    /**
     * Record the timestamp of last modification so
     * we can tell the client to use cached result
     */
	@LastModifiedDate
    DateTime lastModified;

	// Provided by AspectJ-ized NodeEntity
	// public abstract Long getNodeId();

	@PartOfUpdate
	public User getOwner() {
		return owner;
	}

	public abstract boolean canBeDeleted();
}
