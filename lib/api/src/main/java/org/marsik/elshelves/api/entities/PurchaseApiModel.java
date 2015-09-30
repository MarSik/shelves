package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.PurchaseIdResolver;

import java.util.Set;
import java.util.UUID;

/**
 * The initial lot created when parts are purchased
 */
@Data
@EqualsAndHashCode(of = {}, callSuper = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = PurchaseIdResolver.class)
@EmberModelName("purchase")
public class PurchaseApiModel extends AbstractEntityApiModel {
	public PurchaseApiModel(UUID id) {
		super(id);
	}

	public PurchaseApiModel() {
	}

	Long count;

	Double singlePrice;
    Double totalPrice;
    Double vat;
    Boolean vatIncluded;
    Long missing;

	TransactionApiModel transaction;

	PartTypeApiModel type;

	Set<LotApiModel> lots;

	@JsonIdentityReference(alwaysAsId = true)
	public TransactionApiModel getTransaction() {
		return transaction;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public PartTypeApiModel getType() {
		return type;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public Set<LotApiModel> getLots() {
		return lots;
	}
}
