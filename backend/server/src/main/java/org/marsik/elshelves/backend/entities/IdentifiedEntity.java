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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
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

    protected interface RemoteUpdater<T, L> {
        void update(T inst, L value);
    }

    protected interface Updater<T> {
        void update(T value);
    }

    protected interface RemoteGetter<T, L> {
        Collection<L> get(T instance);
    }

    protected static <T> void update(T value, Updater<T> cb) {
        if (value != null) {
            cb.update(value);
        }
    }

    // XXX Why remote updater? Isn't local enough?
    protected static <L extends IdentifiedEntity,T> void reconcileLists(L self, L update, RemoteGetter<L, T> local, RemoteUpdater<L, ? super T> adder, RemoteUpdater<L, ? super T> remover) {
        if (local.get(update) == null) {
            return;
        }

        for (T el : (T[])local.get(self).toArray()) {
            if (!local.get(update).contains(el)) {
                remover.update(self, el);
            }
        }

        for (T el: local.get(update)) {
            if (!local.get(self).contains(el)) {
                adder.update(self, el);
            }
        }
    }
}
