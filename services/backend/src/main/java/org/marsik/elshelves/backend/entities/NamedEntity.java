package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
public class NamedEntity extends OwnedEntity {
	@NotEmpty
	@NotNull
	String name;

	String summary;

    boolean flagged = false;

	String description;

	@OneToMany(mappedBy = "describes")
	Set<Document> describedBy;

	@OneToMany(mappedBy = "entity")
    Set<NumericPropertyValue> properties;

    /**
     * Barcode associated with this entity
     */
	@OneToMany(mappedBy = "reference")
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
