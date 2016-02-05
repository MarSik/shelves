package org.marsik.elshelves.backend.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.marsik.elshelves.backend.services.UuidGenerator;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DefaultEmberModel(LotApiModel.class)
public class MixedLot extends Lot {
    @NotNull
    @NotEmpty
    @JoinTable(name = "mixed_lot_parents")
    @OneToMany(fetch = FetchType.LAZY)
    Set<Lot> parents;

    public boolean addPossibleSource(Lot source) {
        if (!parents.isEmpty()) {
            verifyLotsMixable(parents.iterator().next(), source);
        }
        return parents.add(source);
    }

    public boolean removePossibleSource(Lot source) {
        return parents.remove(source);
    }

    public void addPartsToMix(Lot source) {
        if (addPossibleSource(source)) {
            count += source.getCount();
        }
    }

    public void removePartsFromMix(Lot source) {
        if (removePossibleSource(source)) {
            count -= source.getCount();
        }
    }

    @Override
    public void relink(Relinker relinker) {
        relinkList(relinker, this::getParents, this::addPossibleSource, this::removePossibleSource);
        super.relink(relinker);
    }

    public static MixedLot from(UuidGenerator uuidGenerator, Collection<Lot> lots) {
        if (!Objects.nonNull(lots)) {
            throw new IllegalArgumentException("MixedLot has to have parents");
        }

        if (lots.size() == 0) {
            throw new IllegalArgumentException("MixedLot has to have parents");
        }

        final Lot firstLot = lots.iterator().next();
        LotAction action = firstLot.getStatus();
        Type t = firstLot.getType();
        Requirement req = firstLot.getUsedBy();

        MixedLot mixedLot = new MixedLot();
        mixedLot.setId(uuidGenerator.generate());
        mixedLot.setType(t);
        mixedLot.setCount(0L);
        mixedLot.setStatus(action);
        mixedLot.setUsedBy(req);
        mixedLot.setLocation(firstLot.getLocation());
        mixedLot.setHistory(new LotHistory());
        mixedLot.getHistory().setId(uuidGenerator.generate());
        mixedLot.getHistory().setAction(LotAction.DELIVERY);
        mixedLot.getHistory().setLocation(mixedLot.getLocation());
        mixedLot.getHistory().setAssignedTo(req);
        mixedLot.getHistory().setValidSince(new DateTime());

        for (Lot l : lots) {
            mixedLot.addPartsToMix(l);
        }

        return mixedLot;
    }

    public static MixedLot from(UuidGenerator uuidGenerator, Lot... lots) {
        return from(uuidGenerator, Arrays.asList(lots));
    }

    private static void verifyLotsMixable(Lot origin, Lot l) {
        if (!Objects.equals(origin.getType(), l.getType())) {
            throw new IllegalArgumentException("All lots in MixedLot have to be of the same type");
        }

        if (!Objects.equals(origin.getUsedBy(), l.getUsedBy())) {
            throw new IllegalArgumentException("All lots in MixedLot have to be assigned to the same requirement");
        }

        if (!Objects.equals(origin.getStatus(), l.getStatus())) {
            throw new IllegalArgumentException("All lots in MixedLot have to be in the same status");
        }
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
