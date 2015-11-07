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

	@NotNull
	String name;
	String summary;
	String description;
	boolean hasIcon = false;
    boolean flagged = false;
    boolean canBeDeleted = false;
	DateTime created;

	Set<DocumentApiModel> describedBy;

    Map<UUID, Long> values;
    Set<NumericPropertyApiModel> properties;

    Set<CodeApiModel> codes;

	@JsonIdentityReference(alwaysAsId = true)
	public Set<DocumentApiModel> getDescribedBy() {
		return describedBy;
	}

    @JsonIdentityReference(alwaysAsId = true)
    public Set<NumericPropertyApiModel> getProperties() {
        return properties;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public Set<CodeApiModel> getCodes() {
        return codes;
    }

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
