package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.fields.FootprintType;
import org.marsik.elshelves.api.entities.idresolvers.FootprintIdResolver;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id", resolver = FootprintIdResolver.class)
@EmberModelName("footprint")
public class FootprintApiModel extends AbstractNamedEntityApiModel {
	public FootprintApiModel(UUID id) {
		super(id);
	}

	public FootprintApiModel() {
	}

	String kicad;

    Integer pads;
    Integer holes;
    Integer npth;

    FootprintType type;

    Set<FootprintApiModel> seeAlso;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
