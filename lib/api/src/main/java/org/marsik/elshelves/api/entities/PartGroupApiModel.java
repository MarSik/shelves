package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.PartGroupIdResolver;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = PartGroupIdResolver.class)
@EmberModelName("group")
public class PartGroupApiModel extends AbstractNamedEntityApiModel {
	public PartGroupApiModel(UUID id) {
		super(id);
	}

	Set<PartGroupApiModel> groups;
    PartGroupApiModel parent;

    Set<PartTypeApiModel> types;
    Set<NumericPropertyApiModel> showProperties;

	Long directCount;
	Long nestedCount;

    public PartGroupApiModel() {
    }

    public PartGroupApiModel(UUID id, String name) {
        this(id);
        setName(name);
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
