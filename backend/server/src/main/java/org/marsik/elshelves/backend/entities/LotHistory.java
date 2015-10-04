package org.marsik.elshelves.backend.entities;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
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

    @Override
    public void relink(Relinker relinker) {
        relinkItem(relinker, getPrevious(), this::setPrevious);
        relinkItem(relinker, getPerformedBy(), this::setPerformedBy);
        relinkItem(relinker, getLocation(), this::setLocation);
        relinkItem(relinker, getAssignedTo(), this::setAssignedTo);

        super.relink(relinker);
    }
}
