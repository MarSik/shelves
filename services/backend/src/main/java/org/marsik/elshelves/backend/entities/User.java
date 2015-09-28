package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;
import org.joda.time.DateTime;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@NoArgsConstructor
@ToString(of = {}, callSuper = true)
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
public class User extends OwnedEntity implements StickerCapable {
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

    //@OneToMany
    //Set<Authorization> authorizations;

    public UUID getId() {
        return id;
    }

    public void setId(UUID uuid) {
        this.id = uuid;
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
}
