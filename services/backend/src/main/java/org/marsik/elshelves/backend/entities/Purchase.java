package org.marsik.elshelves.backend.entities;

import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.NotNull;
import java.util.Set;

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

	@Query("START p=node({self}) MATCH (p) -[:DELIVERED_AS]-> () -[:NEXT*0..]-> (l:Lot) WHERE NOT (l) -[:NEXT]-> () RETURN l")
	Iterable<Lot> lots;

	public Double getSinglePrice() {
		return singlePrice;
	}

	public void setSinglePrice(Double singlePrice) {
		this.singlePrice = singlePrice;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Double getVat() {
		return vat;
	}

	public void setVat(Double vat) {
		this.vat = vat;
	}

	public Boolean getVatIncluded() {
		return vatIncluded;
	}

	public void setVatIncluded(Boolean vatIncluded) {
		this.vatIncluded = vatIncluded;
	}

	public Source getSource() {
		Transaction transaction1 = getTransaction();
		return (transaction1 == null) ? null : transaction1.getSource();
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public boolean canBeDeleted() {
		// Purchase can be deleted only if it has not been used yet
		return !getNext().iterator().hasNext();
	}

	@Override
	public boolean canBeUpdated() {
		// Purchase can be updated only if it has not been used yet
		return canBeDeleted();
	}

	@Override
	public Set<Lot> getNext() {
		return next;
	}

	public void setNext(Set<Lot> next) {
		this.next = next;
	}

	public Iterable<Lot> getLots() {
		return lots;
	}

	public void setLots(Iterable<Lot> lots) {
		this.lots = lots;
	}
}
