package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
public abstract class LotBase extends OwnedEntity {
	@NotNull
	Date created;

	@NotNull
	@Min(1)
	Long count;

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
