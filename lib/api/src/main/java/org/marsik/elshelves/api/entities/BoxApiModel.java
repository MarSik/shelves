package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.BoxIdResolver;

import java.util.Set;
import java.util.UUID;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = BoxIdResolver.class)
@EmberModelName("box")
@Getter
@Setter
public class BoxApiModel extends AbstractNamedEntityApiModel {
	public BoxApiModel(UUID id) {
		super(id);
	}

	public BoxApiModel() {
	}

    Set<LotApiModel> lots;

    BoxApiModel parent;

    Set<BoxApiModel> boxes;


    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
