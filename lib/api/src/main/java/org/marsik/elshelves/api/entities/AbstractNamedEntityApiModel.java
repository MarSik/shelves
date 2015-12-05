package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class AbstractNamedEntityApiModel extends AbstractOwnedEntityApiModel {
	public AbstractNamedEntityApiModel(UUID id) {
		super(id);
	}

	public AbstractNamedEntityApiModel() {
	}

	public AbstractNamedEntityApiModel(String uuid) {
		super(uuid);
	}

	@NotNull
	String name;
	String summary;
	String description;
	Boolean hasIcon;
    Boolean flagged;
    Boolean canBeDeleted;
	DateTime created;

	Set<DocumentApiModel> describedBy;

    Map<UUID, Long> values;
    Set<NumericPropertyApiModel> properties;

    Set<CodeApiModel> codes;

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
