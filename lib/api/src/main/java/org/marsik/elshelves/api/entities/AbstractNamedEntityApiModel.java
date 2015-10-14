package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = {}, callSuper = true)
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
}
