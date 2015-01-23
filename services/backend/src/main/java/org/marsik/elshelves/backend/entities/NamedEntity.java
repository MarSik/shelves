package org.marsik.elshelves.backend.entities;

import org.hibernate.validator.constraints.NotEmpty;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.annotation.RelatedToVia;

import javax.validation.constraints.NotNull;
import java.util.Set;

@NodeEntity
public class NamedEntity extends OwnedEntity {
	@Indexed
	@NotEmpty
	@NotNull
	String name;

	@Indexed
	String summary;

	@Indexed
	String description;

	@RelatedTo(type = "DESCRIBES", direction = Direction.INCOMING)
	Set<Document> describedBy;

    @RelatedToVia(type = "HAS_PROPERTY", elementClass = NumericPropertyValue.class)
    Set<NumericPropertyValue> properties;

	@PartOfUpdate
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@PartOfUpdate
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	@PartOfUpdate
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@PartOfUpdate
	public Set<Document> getDescribedBy() {
		return describedBy;
	}

	public void setDescribedBy(Set<Document> describedBy) {
		this.describedBy = describedBy;
	}

	@Override
	public boolean canBeDeleted() {
		return false;
	}

    @PartOfUpdate
    public Iterable<NumericPropertyValue> getProperties() {
        return properties;
    }

    public void setProperties(Set<NumericPropertyValue> properties) {
        this.properties = properties;
    }
}
