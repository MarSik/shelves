package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;
import org.joda.time.DateTime;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString(of = {}, callSuper = true)
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
public class User extends IdentifiedEntity implements OwnedEntityInterface, StickerCapable, UpdateableEntity {
    @NotNull
    @Size(max = 255)
    String name;

    @NotNull
    @Email
    @Size(max = 255)
    String email;
    String password;

    String verificationCode;

    @org.hibernate.annotations.Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	DateTime verificationStartTime;

    @CreatedDate
    @org.hibernate.annotations.Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    DateTime registrationDate;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Authorization> authorizations = new THashSet<>();

    public void addAuthorization(Authorization a) {
        a.setOwner(this);
    }

    public void removeAuthorization(Authorization a) {
        a.setOwner(null);
    }

	@PartOfUpdate
    public String getName() {
        return name;
    }

	@PartOfUpdate
    public String getEmail() {
        return email;
    }

    @Override
	public User getOwner() {
		return this;
	}

	@Override
	public void setOwner(User user) {
	    // NOP
	}

	// Special logic handles user removal
	@Override
	public boolean canBeDeleted() {
		return false;
	}

    @Override
    public boolean canBeUpdated() {
        return true;
    }

    // Sticker info
	@Override
	public String getSummary() {
		return getEmail();
	}

	@Override
	public String getBaseUrl() {
		return "users";
	}

    public void updateFrom(UpdateableEntity update0) {
        if (!(update0 instanceof User)) {
            throw new IllegalArgumentException();
        }

        User update = (User)update0;

        update(update.getName(), this::setName);
        update(update.getEmail(), this::setEmail);

        reconcileLists(this, update, User::getAuthorizations, User::addAuthorization, User::removeAuthorization);
    }
}
