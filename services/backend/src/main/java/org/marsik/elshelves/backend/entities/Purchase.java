package org.marsik.elshelves.backend.entities;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString(of = {}, callSuper = true)
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
public class Purchase extends LotBase {
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Type type;

	Double singlePrice;
	Double totalPrice;
	Double vat;
	Boolean vatIncluded;

	@NotNull
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Transaction transaction;

    @OneToMany(mappedBy = "purchase",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Set<Lot> rawLots;

	@PartOfUpdate
	public Double getSinglePrice() {
		return singlePrice;
	}

	@PartOfUpdate
	public Double getTotalPrice() {
		return totalPrice;
	}

	@PartOfUpdate
	public Double getVat() {
		return vat;
	}

	@PartOfUpdate
	public Boolean getVatIncluded() {
		return vatIncluded;
	}

	public Source getSource() {
		Transaction transaction1 = getTransaction();
		return (transaction1 == null) ? null : transaction1.getSource();
	}

	@PartOfUpdate
	public Transaction getTransaction() {
		return transaction;
	}

	@PartOfUpdate
	public Type getType() {
		return type;
	}

	@Override
	public boolean canBeDeleted() {
		// Purchase can be deleted only if it has not been used yet
		return !getNext().iterator().hasNext();
	}

	@Override
	public boolean canBeUpdated() {
		// Purchase data can be updated
		return true;
	}

    public Long getMissing() {
        if (getLots() == null) return 0L;

        Long count = getCount();
        for (Lot l: getNext()) {
            count -= l.getCount();
        }

        return count;
    }

    /**
     * Return all lots that are currently at the end of the dependency tree
     * - the actual lots that are available for manipulation
     */
    public Iterable<Lot> getLots() {
        return FluentIterable.from(getRawLots())
                .filter(new Predicate<Lot>() {
                    @Override
                    public boolean apply(Lot lot) {
                        return !lot.getNext().iterator().hasNext();
                    }
                });
    }

    /**
     * Return all lots that are roots of the lot dependency tree - the actual
     * lots that were originally assigned to the Purchase.
     */
    public Iterable<Lot> getNext() {
        return FluentIterable.from(getRawLots())
                .filter(new Predicate<Lot>() {
                    @Override
                    public boolean apply(Lot lot) {
                        return lot.getPrevious() == null;
                    }
                });
    }
}
