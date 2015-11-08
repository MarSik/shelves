package org.marsik.elshelves.backend.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.interfaces.Relinker;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class LotHistory extends IdentifiedEntity {
    @Builder(toBuilder = true)
    protected LotHistory(UUID id,
                         LotHistory previous,
                         User performedBy,
                         DateTime created,
                         LotAction action,
                         Box location,
                         Requirement assignedTo) {
        this.id = id;
        this.previous = previous;
        this.performedBy = performedBy;
        this.validSince = created;
        this.action = action;
        this.location = location;
        this.assignedTo = assignedTo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    LotHistory previous;

    @ManyToOne(fetch = FetchType.LAZY)
    User performedBy;

    @NotNull
    @org.hibernate.annotations.Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    DateTime validSince;

    @NotNull
    @Enumerated(EnumType.STRING)
    LotAction action;

    @ManyToOne(fetch = FetchType.LAZY)
    Box location;

    @ManyToOne(fetch = FetchType.LAZY)
    Requirement assignedTo;

    @Override
    public void relink(Relinker relinker) {
        relinkItem(relinker, getPrevious(), this::setPrevious);
        relinkItem(relinker, getPerformedBy(), this::setPerformedBy);
        relinkItem(relinker, getLocation(), this::setLocation);
        relinkItem(relinker, getAssignedTo(), this::setAssignedTo);

        super.relink(relinker);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
