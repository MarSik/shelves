package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.marsik.elshelves.api.ember.EmberModelName;

import java.util.UUID;

/**
 * The initial lot created when parts are purchased
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@EmberModelName("purchase")
public class PurchaseApiModel extends LotApiModel {
    Double singlePrice;
    Double totalPrice;
    Double vat;
    Boolean vatIncluded;

    SourceApiModel source;

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
	public SourceApiModel getSource() {
		return source;
	}

	@JsonIgnore
	public void setSource(SourceApiModel source) {
		this.source = source;
	}

	@JsonSetter
	public void setSource(UUID source) {
		this.source = new SourceApiModel();
		this.source.setId(source);
	}
}
