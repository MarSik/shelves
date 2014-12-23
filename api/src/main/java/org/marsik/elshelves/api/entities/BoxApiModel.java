package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import gnu.trove.map.hash.THashMap;
import nl.marcus.ember.EmberIgnore;
import org.marsik.elshelves.api.ember.EmberModelName;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@EmberModelName("box")
public class BoxApiModel extends AbstractEntityApiModel {
    @NotNull
    String name;
    CodeApiModel code;

    List<LotApiModel> lots;

    UserApiModel belongsTo;
    BoxApiModel parent;
    List<BoxApiModel> boxes;

    @Override
    @EmberIgnore
    public Map<String, String> getLinks() {
        Map<String, String> links = new THashMap<String, String>();
        links.put("lots", "lots");
		links.put("boxes", "boxes");
        return links;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public List<LotApiModel> getLots() {
        return lots;
    }

    @JsonSetter
    public void setLots(List<LotApiModel> lots) {
        this.lots = lots;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public BoxApiModel getParent() {
        return parent;
    }

    @JsonIgnore
    public void setParent(BoxApiModel parent) {
        this.parent = parent;
    }

    @JsonSetter
    public void setParent(UUID parent) {
        this.parent = new BoxApiModel();
        this.parent.setId(parent);
    }

    @JsonIdentityReference(alwaysAsId = true)
    public List<BoxApiModel> getBoxes() {
        return boxes;
    }

    @JsonSetter
    public void setBoxes(List<BoxApiModel> boxes) {
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

    @JsonIdentityReference(alwaysAsId = true)
    public UserApiModel getBelongsTo() {
        return belongsTo;
    }

    @JsonIgnore
    public void setBelongsTo(UserApiModel belongsTo) {
        this.belongsTo = belongsTo;
    }

    @JsonSetter
    public void setBelongsTo(UUID belongsTo) {
        this.belongsTo = new UserApiModel();
        this.belongsTo.setId(belongsTo);
    }
}
