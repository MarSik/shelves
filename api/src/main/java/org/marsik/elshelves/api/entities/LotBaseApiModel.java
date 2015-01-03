package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.marsik.elshelves.api.ember.Sideload;
import org.marsik.elshelves.api.entities.deserializers.LotIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.PartTypeIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.UserIdDeserializer;

import javax.validation.constraints.Min;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class LotBaseApiModel extends AbstractEntityApiModel {
	Date created;
	@Min(1)
	Long count;

	Set<LotApiModel> next;

	@Sideload
    UserApiModel belongsTo;

	@Sideload
	UserApiModel performedBy;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public Set<LotApiModel> getNext() {
		return next;
	}

	@JsonSetter
	@JsonDeserialize(contentUsing = LotIdDeserializer.class)
	public void setNext(Set<LotApiModel> next) {
		this.next = next;
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
	public UserApiModel getPerformedBy() {
		return performedBy;
	}

	@JsonSetter
	@JsonDeserialize(using = UserIdDeserializer.class)
	public void setPerformedBy(UserApiModel performedBy) {
		this.performedBy = performedBy;
	}
}
