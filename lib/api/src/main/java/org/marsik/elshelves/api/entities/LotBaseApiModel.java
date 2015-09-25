package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;

import javax.validation.constraints.Min;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = {}, callSuper = true)
public class LotBaseApiModel extends AbstractOwnedEntityApiModel {
	public LotBaseApiModel(UUID id) {
		super(id);
	}

	public LotBaseApiModel() {
	}

	DateTime created;
	@Min(1)
	Long count;

	Set<LotApiModel> next;

	UserApiModel performedBy;

	@JsonIdentityReference(alwaysAsId = true)
	public Set<LotApiModel> getNext() {
		return next;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public UserApiModel getPerformedBy() {
		return performedBy;
	}
}
