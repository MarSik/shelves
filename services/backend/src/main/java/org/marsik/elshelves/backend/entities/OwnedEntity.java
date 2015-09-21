package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"uuid"})
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "uuid"))
public abstract class OwnedEntity {
	private static final Logger log = LoggerFactory.getLogger(OwnedEntity.class);

	@ManyToOne
	User owner;

    @Id
    @GeneratedValue
    Long id;

	@Column(columnDefinition = "BINARY(16)")
	UUID uuid;

    /**
     * Record the timestamp of last modification so
     * we can tell the client to use cached result
     */
	@LastModifiedDate
	@org.hibernate.annotations.Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    DateTime lastModified;

	@PartOfUpdate
	public User getOwner() {
		return owner;
	}

	public abstract boolean canBeDeleted();

	@Override
	public String toString() {
		return getClass().getName() + "{" +
				"id=" + id +
				", uuid=" + uuid +
				'}';
	}

	@PrePersist
	void prePersist() {
		log.debug("Saving "+toString());
	}
}
