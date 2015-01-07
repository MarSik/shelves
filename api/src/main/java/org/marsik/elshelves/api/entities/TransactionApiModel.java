package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.ember.Sideload;
import org.marsik.elshelves.api.entities.idresolvers.TransactionIdResolver;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = TransactionIdResolver.class)
@EmberModelName("transaction")
public class TransactionApiModel extends AbstractEntityApiModel {
	public TransactionApiModel(UUID id) {
		super(id);
	}

	public TransactionApiModel() {
	}

	String name;
	Date date;

	Set<PurchaseApiModel> items;

	UserApiModel belongsTo;

	SourceApiModel source;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public Set<PurchaseApiModel> getItems() {
		return items;
	}

	@JsonSetter
	public void setItems(Set<PurchaseApiModel> items) {
		this.items = items;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public UserApiModel getBelongsTo() {
		return belongsTo;
	}

	@JsonSetter
	public void setBelongsTo(UserApiModel belongsTo) {
		this.belongsTo = belongsTo;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public SourceApiModel getSource() {
		return source;
	}

	@JsonSetter
	public void setSource(SourceApiModel source) {
		this.source = source;
	}
}
