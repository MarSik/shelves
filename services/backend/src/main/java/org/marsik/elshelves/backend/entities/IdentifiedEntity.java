package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import java.util.UUID;

@Entity
@EqualsAndHashCode(of = {"id"})
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "id"))
@EntityListeners(AuditingEntityListener.class)
public class IdentifiedEntity implements IdentifiedEntityInterface {
    @Id
    @GeneratedValue
    Long dbId;

    @Column(columnDefinition = "BINARY(16)")
    UUID id;

    /**
     * Record the timestamp of last modification so
     * we can tell the client to use cached result
     */
	@LastModifiedDate
	@org.hibernate.annotations.Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    DateTime lastModified;

    @CreatedDate
    @org.hibernate.annotations.Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    DateTime created;

    @Transient
    public boolean isNew() {
        return dbId == null;
    }

    protected interface Updater<T> {
        void update(T value);
    }

    protected static <T> void update(T value, Updater<T> cb) {
        if (value != null) {
            cb.update(value);
        }
    }
}
