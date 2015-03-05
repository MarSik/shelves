package org.marsik.elshelves.backend.entities;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NodeEntity
public class Authorization extends OwnedEntity {
    @NotEmpty
    @NotNull
    String name;

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
    String secret;

    Date created;
    Date lastUsed;

    @Override
    public boolean canBeDeleted() {
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }
}
