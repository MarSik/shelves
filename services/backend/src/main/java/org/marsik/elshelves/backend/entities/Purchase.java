package org.marsik.elshelves.backend.entities;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.time.DateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Type type;

	@NotNull
	@Min(1)
	Long count;

	@NotNull
	@org.hibernate.annotations.Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	DateTime created;

	Double singlePrice;
	Double totalPrice;
	Double vat;
	Boolean vatIncluded;

	@NotNull
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Transaction transaction;

    @OneToMany(mappedBy = "purchase",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Set<Lot> lots;

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
}
