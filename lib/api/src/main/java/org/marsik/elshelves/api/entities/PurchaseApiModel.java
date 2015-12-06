package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.PurchaseIdResolver;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * The initial lot created when parts are purchased
 */
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id", resolver = PurchaseIdResolver.class)
@JsonTypeName("purchase")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "_type",
		visible = true,
		defaultImpl = PurchaseApiModel.class)
public class PurchaseApiModel extends AbstractEntityApiModel {
	public PurchaseApiModel(UUID id) {
		super(id);
	}

	public PurchaseApiModel() {
	}

	public PurchaseApiModel(String uuid) {
		super(uuid);
	}

	Long count;

	@Min(0)
	BigDecimal singlePrice;
	@Min(0)
    BigDecimal totalPrice;

	@Size(min = 3, max = 3)
	String currency;

	@Min(0)
	BigDecimal singlePricePaid;
	@Min(0)
	BigDecimal totalPricePaid;

	@Size(min = 3, max = 3)
	String currencyPaid;

	@Min(0)
	BigDecimal vat;
    Boolean vatIncluded;
    Long missing;
	String sku;

	TransactionApiModel transaction;

	PartTypeApiModel type;

	Set<LotApiModel> lots;


	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
