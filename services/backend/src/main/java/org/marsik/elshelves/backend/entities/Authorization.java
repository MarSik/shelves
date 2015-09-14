package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NodeEntity
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
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

    @PartOfUpdate
    public String getName() {
        return name;
    }

    @PartOfUpdate
    public String getSecret() {
        return secret;
    }
}
