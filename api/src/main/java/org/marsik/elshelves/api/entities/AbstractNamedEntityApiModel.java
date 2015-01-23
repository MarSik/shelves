package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.marsik.elshelves.api.entities.fields.NumericPropertyValueApiModel;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

	UserApiModel belongsTo;

	Set<DocumentApiModel> describedBy;
    List<NumericPropertyValueApiModel> values;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
	public Set<DocumentApiModel> getDescribedBy() {
		return describedBy;
	}

	@JsonSetter
	public void setDescribedBy(Set<DocumentApiModel> describedBy) {
		this.describedBy = describedBy;
	}

	public boolean isHasIcon() {
		return hasIcon;
	}

	public void setHasIcon(boolean hasIcon) {
		this.hasIcon = hasIcon;
	}

    public List<NumericPropertyValueApiModel> getValues() {
        return values;
    }

    public void setValues(List<NumericPropertyValueApiModel> values) {
        this.values = values;
    }
}
