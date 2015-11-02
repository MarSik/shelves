package org.marsik.elshelves.backend.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Authorization extends IdentifiedEntity implements OwnedEntityInterface, UpdateableEntity {
    @NotEmpty
    @NotNull
    @Size(min = 1, max = 255)
    String name;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    User owner;

    public void setOwner(User u) {
        if (owner != null) owner.getAuthorizations().add(this);
        owner = u;
        if (owner != null) owner.getAuthorizations().remove(this);
    }

    public void unsetOwner(User u) {
        assert u.equals(owner);
        setOwner(null);
    }

    /**
     * The client device contains the UUID (I) of this entity,
     * secret code used to generate the bcrypt sequence (S)
     *
     * The user knows his personal secret U.
     *
     * The secret stored in this entity is crypt(ISU)
     *
     * The client sends P = crypt(ISU) as a password.
     */
    @NotEmpty
    @Size(max = 255)
    String secret;

    @CreatedDate
    @org.hibernate.annotations.Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    DateTime created;

    @org.hibernate.annotations.Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    DateTime lastUsed;

    @Override
    public boolean canBeDeleted() {
        return true;
    }

    @Override
    public boolean canBeUpdated() {
        return true;
    }

    @Override
    public void updateFrom(UpdateableEntity update0) throws OperationNotPermitted {
        if (!(update0 instanceof Authorization)) {
            return;
        }

        Authorization update = (Authorization) update0;

        update(update.getName(), this::setName);
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
