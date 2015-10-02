package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.fields.FootprintType;
import org.marsik.elshelves.api.entities.idresolvers.FootprintIdResolver;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = {}, callSuper = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = FootprintIdResolver.class)
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

    @JsonIdentityReference(alwaysAsId = true)
    public Set<FootprintApiModel> getSeeAlso() {
        return seeAlso;
    }
}
