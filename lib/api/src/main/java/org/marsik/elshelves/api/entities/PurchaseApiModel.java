package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.PurchaseIdResolver;

import java.util.Set;
import java.util.UUID;

/**
 * The initial lot created when parts are purchased
 */
@Getter
@Setter
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

	Set<PolymorphicRecord> lots;

	@JsonIdentityReference(alwaysAsId = true)
	public TransactionApiModel getTransaction() {
		return transaction;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public PartTypeApiModel getType() {
		return type;
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
