package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;
import org.marsik.elshelves.backend.interfaces.Relinker;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        Collection<T> get();
    }

    protected static <T> void update(T value, Updater<T> cb) {
        if (value != null) {
            cb.update(value);
        }
    }

    protected static <T> void reconcileLists(Collection<T> update, Getter<T> local, Updater<? super T> adder, Updater<? super T> remover) {
        reconcileLists(update, local, adder, remover, false);
    }

    protected static <T> void reconcileLists(Collection<T> update, Getter<T> local, Updater<? super T> adder, Updater<? super T> remover, boolean forceRefresh) {
        if (update == null) {
            return;
        }

        for (T el : (T[])local.get().toArray()) {
            if (forceRefresh || !update.contains(el)) {
                remover.update(el);
            }
        }

        for (T el: update) {
            if (forceRefresh || !local.get().contains(el)) {
                adder.update(el);
            }
        }
    }

    public void relink(Relinker relinker) {

    }

    protected <T extends IdentifiedEntity> void relinkItem(Relinker relinker, T item, Updater<T> setter) {
        setter.update((T)relinker.relink(item));
    }

    protected <T extends IdentifiedEntity> void relinkList(Relinker relinker, Getter<T> getter, Updater<T> adder, Updater<T> remover) {
        List<T> updates = new ArrayList<>();
        for (T el: getter.get()) {
            updates.add((T)relinker.relink(el));
        }

        reconcileLists(updates, getter, adder, remover, true);
    }
}
