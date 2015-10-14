package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.interfaces.Relinker;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DefaultEmberModel(PurchaseApiModel.class)
public class Purchase extends OwnedEntity {
	@ManyToOne(optional = false)
	Type type;

	public void setType(Type t) {
		if (type != null) type.getPurchases().remove(this);
		type = t;
		if (type != null) type.getPurchases().add(this);
	}

	public void unsetType(Type t) {
		assert t.equals(type);
		setType(null);
	}

	@NotNull
	@Min(1)
	Long count;

	Double singlePrice;
	Double totalPrice;
	Double vat;
	Boolean vatIncluded;

	@NotNull
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE },
			optional = false)
	Transaction transaction;

	public void setTransaction(Transaction t) {
		if (transaction != null) transaction.getItems().remove(this);
		transaction = t;
		if (transaction != null) transaction.getItems().add(this);
	}

	public void unsetTransaction(Transaction t) {
		assert t.equals(transaction);
		setTransaction(null);
	}

    @OneToMany(mappedBy = "purchase",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Set<Lot> lots = new THashSet<>();

	public void addLot(Lot l) {
		l.setPurchase(this);
	}

	public void removeLot(Lot l) {
		l.setPurchase(null);
	}

	public Source getSource() {
		Transaction transaction1 = getTransaction();
		return (transaction1 == null) ? null : transaction1.getSource();
	}

	@Override
	public boolean canBeDeleted() {
		// Purchase can be deleted only if it has not been used yet
		return getLots().isEmpty();
	}

	@Override
	public boolean canBeUpdated() {
		// Purchase data can be updated
		return true;
	}

    public Long getMissing() {
        if (getLots() == null) return 0L;

        Long count = getCount();
        for (Lot l: getLots()) {
            count -= l.getCount();
        }

        return count;
    }

	@Override
	public void updateFrom(UpdateableEntity update0) {
		if (!(update0 instanceof Purchase)) {
			throw new IllegalArgumentException();
		}

		Purchase update = (Purchase)update0;

		update(update.getSinglePrice(), this::setSinglePrice);
		update(update.getTotalPrice(), this::setTotalPrice);
		update(update.getVat(), this::setVat);
		update(update.getVatIncluded(), this::setVatIncluded);
		update(update.getType(), this::setType);

		super.updateFrom(update0);
	}

	@Override
	public void relink(Relinker relinker) {
		relinkItem(relinker, getTransaction(), this::setTransaction);
		relinkItem(relinker, getType(), this::setType);
		relinkList(relinker, this::getLots, this::addLot, this::removeLot);
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

	@PrePersist
	void prePersist() {
		if (getSinglePrice() == null
				&& getCount() != null
				&& getTotalPrice() != null) {
			setSinglePrice(getTotalPrice() / getCount());
		}

		if (getTotalPrice() == null
				&& getCount() != null
				&& getSinglePrice() != null) {
			setTotalPrice(getSinglePrice() * getCount());
		}

		super.prePersist();
	}
}
