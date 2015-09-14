package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@NodeEntity
public class Purchase extends LotBase {
	@NotNull
	@RelatedTo(type = "OF_TYPE")
	Type type;

	Double singlePrice;
	Double totalPrice;
	Double vat;
	Boolean vatIncluded;

	@NotNull
	@RelatedTo(type = "IN_TRANSACTION")
	Transaction transaction;

	@RelatedTo(type = "DELIVERED_AS")
	Set<Lot> next;

	@Query("START p=node({self}) MATCH (p) -[:DELIVERED_AS]-> (:Lot) <-[:TAKEN_FROM*0..]- (l:Lot) WHERE NOT (l) <-[:TAKEN_FROM]- (:Lot) RETURN l")
	Iterable<Lot> lots;

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
}
