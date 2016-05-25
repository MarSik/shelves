package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.javamoney.moneta.Money;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.interfaces.Relinker;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DefaultEmberModel(PurchaseApiModel.class)
public class Purchase extends OwnedEntity {
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	Type type;

	@OneToOne(fetch = FetchType.LAZY)
	Sku sku;

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

	/**
	 * TotalPrice is reusing the currency of singlePrice and only the number is stored in the database.
	 * The currency is stripped/restored during update/loading time.
	 */

	@Column(name = "currency")
	@Size(min = 3, max = 3)
	private String currency;

	@Column(name = "paid_currency")
	@Size(min = 3, max = 3)
	private String currencyPaid;

	@Column(name = "single_price")
	private BigDecimal singlePriceRaw;

	@Column(name = "total_price")
	private BigDecimal totalPriceRaw;

	/**
	 * TotalPricePaid is reusing the currency of singlePricePaid and only the number is stored in the database.
	 * The currency is stripped/restored during update/loading time.
	 */
	@Column(name = "paid_single_price")
	private BigDecimal singlePricePaidRaw;

	@Column(name = "paid_total_price")
	private BigDecimal totalPricePaidRaw;


	@Transient
	Money singlePrice;

	/**
	 * The currency of totalPrice is ignored in database and is always assumed
	 * to be equal to the currency of singlePrice.
	 */
	@Transient
	Money totalPrice;

	@Transient
	Money singlePricePaid;

	/**
	 * The currency of totalPricePaid is ignored in database and is always assumed
	 * to be equal to the currency of singlePricePaid.
	 */
	@Transient
	Money totalPricePaid;


	BigDecimal vat;
	Boolean vatIncluded;

	@NotNull
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
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

    @OneToMany(mappedBy = "purchase", fetch = FetchType.LAZY)
	Set<PurchasedLot> lots = new THashSet<>();

	public void addLot(PurchasedLot l) {
		l.setPurchase(this);
	}

	public void removeLot(PurchasedLot l) {
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
	public void updateFrom(UpdateableEntity update0) throws OperationNotPermitted {
		if (!(update0 instanceof Purchase)) {
			throw new IllegalArgumentException();
		}

		Purchase update = (Purchase)update0;

		update(update.getSinglePrice(), this::setSinglePrice);
		update(update.getTotalPrice(), this::setTotalPrice);
		update(update.getVat(), this::setVat);
		update(update.getVatIncluded(), this::setVatIncluded);
		update(update.getType(), this::setType);

		if (willUpdate(getSku(), update.getSku())
				&& !update.getSku().sameContent(getSku())) {
			update(update.getSku(), this::setSku);
		}

		super.updateFrom(update0);
	}

	@Override
	public void relink(Relinker relinker) {
		relinkItem(relinker, getTransaction(), this::setTransaction);
		relinkItem(relinker, getType(), this::setType);
		relinkItem(relinker, getSku(), this::setSku);

		if (getSku() != null && getSku().isNew()) {
			if (getSku().getSource() == null) {
				getSku().setSource(getTransaction().getSource());
			}

			setSku(relinker.findExistingSku(getSku()));
		}

		if (getSku() != null && getSku().isNew()) {
			getSku().relink(relinker);

			if (getSku().getSource() == null) {
				getSku().setSource(getTransaction().getSource());
			}
		}

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

	@PreUpdate
	@PrePersist
	void prePersist() {
		if (getSinglePrice() == null
				&& getCount() != null
				&& getTotalPrice() != null) {
			setSinglePrice(getTotalPrice().divide(getCount()));
		}

		if (getTotalPrice() == null
				&& getCount() != null
				&& getSinglePrice() != null) {
			setTotalPrice(getSinglePrice().multiply(getCount()));
		}

		setIfNotNull(this::setSinglePriceRaw, getSinglePrice(), Money::getNumberStripped);
		setIfNotNull(this::setTotalPriceRaw, getTotalPrice(), Money::getNumberStripped);

		setIfNotNull(this::setSinglePricePaidRaw, getSinglePricePaid(), Money::getNumberStripped);
		setIfNotNull(this::setTotalPricePaidRaw, getTotalPricePaid(), Money::getNumberStripped);

		setIfNotNull(this::setCurrency, getSinglePrice(), object -> object.getCurrency().getCurrencyCode());
		setIfNotNull(this::setCurrencyPaid, getSinglePricePaid(), object -> object.getCurrency().getCurrencyCode());

		super.prePersist();
	}

	@PostLoad
	void postLoad() {
		if (getCurrency() != null) {
			CurrencyUnit currency = Monetary.getCurrency(getCurrency());
			if (getSinglePriceRaw() != null) setSinglePrice(Money.of(getSinglePriceRaw(), currency));
			if (getTotalPriceRaw() != null) setTotalPrice(Money.of(getTotalPriceRaw(), currency));
		}

		if (getCurrencyPaid() != null) {
			CurrencyUnit paidCurrency = Monetary.getCurrency(getCurrencyPaid());
			if (getSinglePricePaidRaw() != null) setSinglePricePaid(Money.of(getSinglePricePaidRaw(), paidCurrency));
			if (getTotalPricePaidRaw() != null) setTotalPricePaid(Money.of(getTotalPricePaidRaw(), paidCurrency));
		}
	}

	private static <E, T> void setIfNotNull(Updater<T> setter, E isNull, RemoteGetter<E, T> getter) {
		if (isNull == null) {
			setter.update(null);
		} else {
			setter.update(getter.get(isNull));
		}
	}

	private <E, T> T returnIfNotNull(E isNull, RemoteGetter<E, T> getter) {
		if (isNull == null) {
			return null;
		} else {
			return getter.get(isNull);
		}
	}
}
