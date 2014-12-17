package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import gnu.trove.map.hash.THashMap;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Box extends AbstractEntity {
    @NotNull
    String name;
    Code code;

    List<Lot> lots;

    User belongsTo;
    Box parent;
    List<Box> boxes;

    @Override
    public Map<String, String> getLinks() {
        Map<String, String> links = new THashMap<String, String>();
        links.put("lots", "lots");
        return links;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public List<Lot> getLots() {
        return lots;
    }

    @JsonSetter
    public void setLots(List<Lot> lots) {
        this.lots = lots;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public Box getParent() {
        return parent;
    }

    @JsonIgnore
    public void setParent(Box parent) {
        this.parent = parent;
    }

    @JsonSetter
    public void setParent(UUID parent) {
        this.parent = new Box();
        this.parent.setId(parent);
    }

    @JsonIdentityReference(alwaysAsId = true)
    public List<Box> getBoxes() {
        return boxes;
    }

    @JsonSetter
    public void setBoxes(List<Box> boxes) {
        this.boxes = boxes;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public Code getCode() {
        return code;
    }

    @JsonIgnore
    public void setCode(Code code) {
        this.code = code;
    }

    @JsonSetter
    public void setCode(UUID code) {
        this.code = new Code();
        this.code.setId(code);
    }

    @JsonIdentityReference(alwaysAsId = true)
    public User getBelongsTo() {
        return belongsTo;
    }

    @JsonIgnore
    public void setBelongsTo(User belongsTo) {
        this.belongsTo = belongsTo;
    }

    @JsonSetter
    public void setBelongsTo(UUID belongsTo) {
        this.belongsTo = new User();
        this.belongsTo.setId(belongsTo);
    }
}
