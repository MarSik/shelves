package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.BoxIdResolver;

import java.util.Set;
import java.util.UUID;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = BoxIdResolver.class)
@EmberModelName("box")
public class BoxApiModel extends AbstractNamedEntityApiModel {
	public BoxApiModel(UUID id) {
		super(id);
	}

	public BoxApiModel() {
	}

    Set<LotApiModel> lots;

    BoxApiModel parent;

    Set<BoxApiModel> boxes;

    @JsonIdentityReference(alwaysAsId = true)
    public Set<LotApiModel> getLots() {
        return lots;
    }

    @JsonSetter
    public void setLots(Set<LotApiModel> lots) {
        this.lots = lots;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public BoxApiModel getParent() {
        return parent;
    }

    @JsonSetter
    public void setParent(BoxApiModel parent) {
        this.parent = parent;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public Set<BoxApiModel> getBoxes() {
        return boxes;
    }

    @JsonSetter
    public void setBoxes(Set<BoxApiModel> boxes) {
        this.boxes = boxes;
    }
}
