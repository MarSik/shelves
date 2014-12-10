package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import gnu.trove.map.hash.THashMap;

import java.util.List;
import java.util.Map;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Box extends AbstractEntity {
    Long id;
    String name;
    Code code;

    List<Lot> lots;

    Box parent;
    List<Box> boxes;

    @Override
    public Map getLinks() {
        Map<String, String> links = new THashMap<>();
        links.put("lots", "lots");
        return links;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public void setParent(Long parent) {
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
    public void setCode(Long code) {
        this.code = new Code();
        this.code.setId(code);
    }
}
