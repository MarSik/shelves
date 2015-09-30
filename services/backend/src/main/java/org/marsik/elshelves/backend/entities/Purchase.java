package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString(of = {}, callSuper = true)
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
public class Purchase extends OwnedEntity {
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE },
			optional = false)
	Type type;

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

    @OneToMany(mappedBy = "purchase",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Set<Lot> lots = new THashSet<>();

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
}
