package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.ember.Sideload;
import org.marsik.elshelves.api.entities.deserializers.PurchaseIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.SourceIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.UserIdDeserializer;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@EmberModelName("transaction")
public class TransactionApiModel extends AbstractEntityApiModel {
	String name;
	Date date;

	@Sideload(asType = PurchaseApiModel.class)
	Set<PurchaseApiModel> items;

	@Sideload
	UserApiModel belongsTo;

	@Sideload
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
	@JsonDeserialize(contentUsing = PurchaseIdDeserializer.class)
	public void setItems(Set<PurchaseApiModel> items) {
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
