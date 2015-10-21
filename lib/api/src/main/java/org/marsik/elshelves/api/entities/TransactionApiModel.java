package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.TransactionIdResolver;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = TransactionIdResolver.class)
@EmberModelName("transaction")
public class TransactionApiModel extends AbstractNamedEntityApiModel {
	public TransactionApiModel(UUID id) {
		super(id);
	}

	public TransactionApiModel() {
	}

	DateTime date;

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

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
