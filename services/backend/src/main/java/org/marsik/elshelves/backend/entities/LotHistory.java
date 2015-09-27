package org.marsik.elshelves.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Data
@NoArgsConstructor
public class LotHistory {
    @Builder(toBuilder = true)
    protected LotHistory(LotHistory previous, User performedBy, DateTime created, LotAction action, Box location, Requirement assignedTo) {
        this.previous = previous;
        this.performedBy = performedBy;
        this.created = created;
        this.action = action;
        this.location = location;
        this.assignedTo = assignedTo;
    }

    @Id
    @GeneratedValue
    Long id;

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
