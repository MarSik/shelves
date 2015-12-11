package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "_type",
		defaultImpl = AbstractNamedEntityApiModel.class,
		visible = true)
@JsonSubTypes({
		@JsonSubTypes.Type(BoxApiModel.class),
		@JsonSubTypes.Type(DocumentApiModel.class),
		@JsonSubTypes.Type(FootprintApiModel.class),
		@JsonSubTypes.Type(ListApiModel.class),
		@JsonSubTypes.Type(NumericPropertyApiModel.class),
		@JsonSubTypes.Type(PartGroupApiModel.class),
		@JsonSubTypes.Type(PartTypeApiModel.class),
		@JsonSubTypes.Type(ProjectApiModel.class),
		@JsonSubTypes.Type(SourceApiModel.class),
		@JsonSubTypes.Type(TransactionApiModel.class),
		@JsonSubTypes.Type(UnitApiModel.class),
})
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
