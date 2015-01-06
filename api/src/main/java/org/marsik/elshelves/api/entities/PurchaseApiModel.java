package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import nl.marcus.ember.EmberIgnore;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.ember.Sideload;
import org.marsik.elshelves.api.entities.idresolvers.PurchaseIdResolver;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * The initial lot created when parts are purchased
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = PurchaseIdResolver.class)
@EmberModelName("purchase")
public class PurchaseApiModel extends LotBaseApiModel {
	public PurchaseApiModel(UUID id) {
		super(id);
	}

	public PurchaseApiModel() {
	}

	Double singlePrice;
    Double totalPrice;
    Double vat;
    Boolean vatIncluded;

	TransactionApiModel transaction;

	PartTypeApiModel type;

	Set<LotApiModel> lots;

	@Override
	@EmberIgnore
	public Map<String, String> getLinks() {
		Map<String, String> links = super.getLinks();
		links.put("next", "next");
		links.put("lots", "lots");
		return links;
	}

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

	@JsonIdentityReference(alwaysAsId = true)
	public TransactionApiModel getTransaction() {
		return transaction;
	}

	@JsonSetter
	public void setTransaction(TransactionApiModel transaction) {
		this.transaction = transaction;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public PartTypeApiModel getType() {
		return type;
	}

	@JsonSetter
	public void setType(PartTypeApiModel type) {
		this.type = type;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public Set<LotApiModel> getLots() {
		return lots;
	}

	@JsonSetter
	public void setLots(Set<LotApiModel> lots) {
		this.lots = lots;
	}
}
