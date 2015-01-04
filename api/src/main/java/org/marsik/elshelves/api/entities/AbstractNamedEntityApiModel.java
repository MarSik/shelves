package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.marsik.elshelves.api.ember.Sideload;
import org.marsik.elshelves.api.entities.deserializers.DocumentIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.UserIdDeserializer;

import javax.validation.constraints.NotNull;
import java.util.Set;

public class AbstractNamedEntityApiModel extends AbstractEntityApiModel {
	@NotNull
	String name;
	String summary;
	String description;

	@Sideload
	UserApiModel belongsTo;

	@Sideload(asType = DocumentApiModel.class)
	Set<DocumentApiModel> describedBy;

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
	@JsonDeserialize(using = UserIdDeserializer.class)
	public void setBelongsTo(UserApiModel belongsTo) {
		this.belongsTo = belongsTo;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public Set<DocumentApiModel> getDescribedBy() {
		return describedBy;
	}

	@JsonSetter
	@JsonDeserialize(contentUsing = DocumentIdDeserializer.class)
	public void setDescribedBy(Set<DocumentApiModel> describedBy) {
		this.describedBy = describedBy;
	}
}
