package org.marsik.elshelves.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners({AuditingEntityListener.class})
public class LotHistory extends IdentifiedEntity {
    @Builder(toBuilder = true)
    protected LotHistory(LotHistory previous, User performedBy, DateTime created, LotAction action, Box location, Requirement assignedTo) {
        this.previous = previous;
        this.performedBy = performedBy;
        this.created = created;
        this.action = action;
        this.location = location;
        this.assignedTo = assignedTo;
    }

    @ManyToOne
    LotHistory previous;

    @ManyToOne
    User performedBy;

    @CreatedDate
    @org.hibernate.annotations.Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    DateTime created;

    LotAction action;

    @ManyToOne
    Box location;

    @ManyToOne
    Requirement assignedTo;
}
