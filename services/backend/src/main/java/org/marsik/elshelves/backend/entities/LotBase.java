package org.marsik.elshelves.backend.entities;

import org.springframework.data.neo4j.annotation.NodeEntity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NodeEntity
public abstract class LotBase extends OwnedEntity {
	@NotNull
	Date created;

	@NotNull
	@Min(1)
	Long count;

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

	public Type getType() {
		return null;
	}

	public boolean canBeDeleted() {
		return false;
	}

	public boolean canBeUpdated() {
		return false;
	}

	public Iterable<Lot> getNext() {
		return null;
	}
}
