package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.annotation.RelatedToVia;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@NodeEntity
public class NamedEntity extends OwnedEntity {
	@Indexed
	@NotEmpty
	@NotNull
	String name;

	@Indexed
	String summary;

    @Indexed
    boolean flagged = false;

	@Indexed
	String description;

	@RelatedTo(type = "DESCRIBES", direction = Direction.INCOMING)
	Set<Document> describedBy;

    @RelatedToVia(type = "HAS_PROPERTY", elementClass = NumericPropertyValue.class)
    Set<NumericPropertyValue> properties;

    /**
     * Barcode associated with this entity
     */
    @RelatedTo(type = "IDENTIFIED_BY")
    Set<Code> codes;

	@PartOfUpdate
	public String getName() {
		return name;
	}

	@PartOfUpdate
	public String getSummary() {
		return summary;
	}

    @PartOfUpdate
	public String getDescription() {
		return description;
	}

	@PartOfUpdate
	public Set<Document> getDescribedBy() {
		return describedBy;
	}

	@Override
	public boolean canBeDeleted() {
		return false;
	}

    @PartOfUpdate
    public Set<NumericPropertyValue> getProperties() {
        return properties;
    }

    public String getEmberType() {
        String type = "unknown";

        DefaultEmberModel emberModelAnnotation = getClass().getAnnotation(DefaultEmberModel.class);
        if (emberModelAnnotation != null) {
            Class<? extends AbstractEntityApiModel> emberModel = emberModelAnnotation.value();
            EmberModelName emberModelName = emberModel.getAnnotation(EmberModelName.class);
            if (emberModelName != null) {
                type = emberModelName.value();
            }
        }

        return type;
    }

    @PartOfUpdate
    public Set<Code> getCodes() {
        return codes;
    }
}
