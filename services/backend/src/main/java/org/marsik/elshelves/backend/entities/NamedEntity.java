package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class NamedEntity extends OwnedEntity
		implements UpdateableEntity {
	@NotEmpty
	@NotNull
	@Size(max = 255)
	String name;

	@Size(max = 255)
	String summary;

    Boolean flagged = false;

	@Lob
	String description;

	@ManyToMany(mappedBy = "describes",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Set<Document> describedBy = new THashSet<>();

	@OneToMany(mappedBy = "entity",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    Set<NumericPropertyValue> properties = new THashSet<>();

    /**
     * Barcode associated with this entity
     */
	@OneToMany(mappedBy = "reference",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    Set<Code> codes = new THashSet<>();

	@Override
	public boolean canBeDeleted() {
		return false;
	}

	@Override
	public boolean canBeUpdated() {
		return true;
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

	@Override
	public String toString() {
		return getClass().getName() + "{" +
				"id=" + dbId +
				", id=" + id +
				", name='" + name + '\'' +
				'}';
	}

	@Override
	public void updateFrom(UpdateableEntity update0) {
		if (!(update0 instanceof NamedEntity)) {
			throw new IllegalArgumentException();
		}

		NamedEntity update = (NamedEntity)update0;

		update(update.getName(), this::setName);
		update(update.getSummary(), this::setSummary);
		update(update.getDescription(), this::setDescription);
		update(update.getFlagged(), this::setFlagged);

		update(update.getDescribedBy(), this::setDescribedBy);
		update(update.getCodes(), this::setCodes);
		update(update.getProperties(), this::setProperties);

		super.updateFrom(update);
	}
}
