package org.marsik.elshelves.backend.entities;

import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@NodeEntity
public class Purchase extends Lot {
	Double singlePrice;
	Double totalPrice;
	Double vat;
	Boolean vatIncluded;

	@RelatedTo(type = "PURCHASED_FROM")
	Source source;

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
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	@Override
	public boolean canBeDeleted() {
		// Purchase can be deleted only if it has not been used yet
		return !getNext().iterator().hasNext() && getPrevious() == null;
	}

	@Override
	public boolean canBeUpdated() {
		// Purchase can be updated only if it has not been used yet
		return canBeDeleted();
	}
}
