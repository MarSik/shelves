package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.validation.constraints.Min;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class LotBaseApiModel extends AbstractEntityApiModel {
	public LotBaseApiModel(UUID id) {
		super(id);
	}

	public LotBaseApiModel() {
	}

	Date created;
	@Min(1)
	Long count;

	Set<LotApiModel> next;

    UserApiModel belongsTo;

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
	public void setNext(Set<LotApiModel> next) {
		this.next = next;
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
	public UserApiModel getPerformedBy() {
		return performedBy;
	}

	@JsonSetter
	public void setPerformedBy(UserApiModel performedBy) {
		this.performedBy = performedBy;
	}
}
