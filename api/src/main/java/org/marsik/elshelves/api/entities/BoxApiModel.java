package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import nl.marcus.ember.EmberIgnore;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.ember.Sideload;
import org.marsik.elshelves.api.entities.idresolvers.BoxIdResolver;

import java.util.Map;
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

	CodeApiModel code;

    Set<LotApiModel> lots;

	@Sideload
    BoxApiModel parent;

    Set<BoxApiModel> boxes;

    @Override
    @EmberIgnore
    public Map<String, String> getLinks() {
        Map<String, String> links = super.getLinks();
        links.put("lots", "lots");
		links.put("boxes", "boxes");
        return links;
    }

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

    @JsonIdentityReference(alwaysAsId = true)
    public CodeApiModel getCode() {
        return code;
    }

    @JsonIgnore
    public void setCode(CodeApiModel code) {
        this.code = code;
    }

    @JsonSetter
    public void setCode(UUID code) {
        this.code = new CodeApiModel();
        this.code.setId(code);
    }

}
