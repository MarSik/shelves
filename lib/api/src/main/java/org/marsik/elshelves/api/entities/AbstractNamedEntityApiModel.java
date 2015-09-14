package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = {}, callSuper = true)
public class AbstractNamedEntityApiModel extends AbstractEntityApiModel {
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

	UserApiModel belongsTo;

	Set<DocumentApiModel> describedBy;

    Map<UUID, Long> values;
    Set<NumericPropertyApiModel> properties;

    Set<CodeApiModel> codes;

	@JsonIdentityReference(alwaysAsId = true)
	public UserApiModel getBelongsTo() {
		return belongsTo;
	}

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
