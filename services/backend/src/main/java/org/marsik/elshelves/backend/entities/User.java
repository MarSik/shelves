package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
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
