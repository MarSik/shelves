package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@ToString(of = {}, callSuper = true)
@EqualsAndHashCode(of = {}, callSuper = true)
public class Authorization extends OwnedEntity {
    @NotEmpty
    @NotNull
    @Size(min = 1, max = 255)
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

    @PartOfUpdate
    public String getName() {
        return name;
    }

    @PartOfUpdate
    public String getSecret() {
        return secret;
    }
}
