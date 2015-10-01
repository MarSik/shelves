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

    protected interface Getter<T> {
        T get();
    }

    protected interface RemoteGetter<T, L> {
        Collection<L> get(T instance);
    }

    protected static <T> void update(T value, Updater<T> cb) {
        if (value != null) {
            cb.update(value);
        }
    }

    protected static <T, L> void updateManyToOne(T value, Updater<T> cb, Getter<T> local, RemoteGetter<T, L> remote, L th) {
        if (value == local.get()
                || (value != null && value.equals(local.get()))
                || (local.get() != null && local.get().equals(value))) {
            return;
        }

        if (local.get() != null) {
            remote.get(local.get()).remove(th);
        }

        if (value != null) {
            cb.update(value);
            remote.get(value).add(th);
        }
    }

    protected static <L extends IdentifiedEntity,T> void updateManyToMany(Set<T> value, Getter<Set<T>> local, RemoteGetter<T, L> remote, L th) {
        for (Iterator<T> it = local.get().iterator(); it.hasNext() ;) {
            T el = it.next();
            if (!value.contains(el)) {
                it.remove();
                remote.get(el).remove(th);
            }
        }

        for (T el: value) {
            if (!local.get().contains(el)) {
                local.get().add(el);
                remote.get(el).add(th);
            }
        }
    }

    protected static <L extends IdentifiedEntity,T> void updateOneToMany(Set<T> value, Getter<Set<T>> local, RemoteUpdater<T, L> remote, L th) {
        for (Iterator<T> it = local.get().iterator(); it.hasNext() ;) {
            T el = it.next();
            if (!value.contains(el)) {
                it.remove();
                remote.update(el, null);
            }
        }

        for (T el: value) {
            if (!local.get().contains(el)) {
                local.get().add(el);
                remote.update(el, th);
            }
        }
    }
}
