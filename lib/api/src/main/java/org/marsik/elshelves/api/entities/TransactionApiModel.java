package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.TransactionIdResolver;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = {}, callSuper = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = TransactionIdResolver.class)
@EmberModelName("transaction")
public class TransactionApiModel extends AbstractNamedEntityApiModel {
	public TransactionApiModel(UUID id) {
		super(id);
	}

	public TransactionApiModel() {
	}

	Date date;

	Set<PurchaseApiModel> items;
	SourceApiModel source;

	@JsonIdentityReference(alwaysAsId = true)
	public Set<PurchaseApiModel> getItems() {
		return items;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public SourceApiModel getSource() {
		return source;
	}
}
