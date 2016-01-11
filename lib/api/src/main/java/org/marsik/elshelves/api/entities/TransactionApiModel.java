package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.TransactionIdResolver;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id", resolver = TransactionIdResolver.class)
@JsonTypeName("transaction")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "_type",
		visible = true,
		defaultImpl = TransactionApiModel.class)
public class TransactionApiModel extends AbstractNamedEntityApiModel {
	public TransactionApiModel(UUID id) {
		super(id);
	}

	public TransactionApiModel() {
	}

	public TransactionApiModel(String uuid) {
		super(uuid);
	}

	DateTime date;
	DateTime expectedDelivery;

	Set<PurchaseApiModel> items;
	SourceApiModel source;

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
