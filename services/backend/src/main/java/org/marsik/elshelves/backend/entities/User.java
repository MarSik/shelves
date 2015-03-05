package org.marsik.elshelves.backend.entities;

import org.apache.http.auth.AUTH;
import org.hibernate.validator.constraints.Email;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@NodeEntity
public class User extends OwnedEntity implements StickerCapable {
    @NotNull
    String name;

    @NotNull
    @Email
	@Indexed
    String email;
    String password;

	@Indexed
    String verificationCode;
	Date verificationStartTime;
    Date registrationDate;

    @RelatedTo(type = "OWNS", enforceTargetType = true)
    Set<Authorization> authorizations;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

	@PartOfUpdate
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	@PartOfUpdate
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

	public Date getVerificationStartTime() {
		return verificationStartTime;
	}

	public void setVerificationStartTime(Date verificationStartTime) {
		this.verificationStartTime = verificationStartTime;
	}

    public Set<Authorization> getAuthorizations() {
        return authorizations;
    }

    public void setAuthorizations(Set<Authorization> authorizations) {
        this.authorizations = authorizations;
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
