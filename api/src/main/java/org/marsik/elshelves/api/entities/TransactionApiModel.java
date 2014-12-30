package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.deserializers.PurchaseIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.SourceIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.UserIdDeserializer;

import java.util.Collection;
import java.util.Date;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@EmberModelName("transaction")
public class TransactionApiModel extends AbstractEntityApiModel {
	String name;
	Date date;
	Collection<PurchaseApiModel> items;
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
	public Collection<PurchaseApiModel> getItems() {
		return items;
	}

	@JsonSetter
	@JsonDeserialize(contentUsing = PurchaseIdDeserializer.class)
	public void setItems(Collection<PurchaseApiModel> items) {
		this.items = items;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public UserApiModel getBelongsTo() {
		return belongsTo;
	}

	@JsonSetter
	@JsonDeserialize(using = UserIdDeserializer.class)
	public void setBelongsTo(UserApiModel belongsTo) {
		this.belongsTo = belongsTo;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public SourceApiModel getSource() {
		return source;
	}

	@JsonSetter
	@JsonDeserialize(using = SourceIdDeserializer.class)
	public void setSource(SourceApiModel source) {
		this.source = source;
	}
}
