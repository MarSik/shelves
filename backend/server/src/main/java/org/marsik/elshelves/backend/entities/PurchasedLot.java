package org.marsik.elshelves.backend.entities;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.marsik.elshelves.backend.services.UuidGenerator;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DefaultEmberModel(LotApiModel.class)
public class PurchasedLot extends Lot {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY,
            optional = false)
    Purchase purchase;

    @Override
    public String getSummary() {
        return getPurchase().getTransaction().getName();
    }

    public void setPurchase(Purchase p) {
        if (purchase != null) purchase.getLots().remove(this);
        purchase = p;
        if (purchase != null) purchase.getLots().add(this);
    }

    public static PurchasedLot delivery(Purchase purchase, UUID uuid, Long count,
                               Box location, DateTime expiration, User performedBy, UuidGenerator uuidGenerator) {
        PurchasedLot l = new PurchasedLot();
        l.setOwner(purchase.getOwner());
        l.setId(uuid);
        l.setLocation(location);
        l.setCount(count);
        l.setPurchase(purchase);
        l.setExpiration(expiration);
        l.setStatus(LotAction.DELIVERY);
        l.setType(purchase.getType());

        LotHistory h = l.createRevision(null, uuidGenerator, performedBy);
        h.setLocation(location);

        return l;
    }

    @Override
    public void relink(Relinker relinker) {
        relinkItem(relinker, getPurchase(), this::setPurchase);
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
