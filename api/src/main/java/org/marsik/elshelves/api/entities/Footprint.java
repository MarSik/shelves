package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Footprint extends AbstractEntity {
    Long id;
    String name;
    String kicad;

    Integer pads;
    Integer holes;
    Integer npth;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKicad() {
        return kicad;
    }

    public void setKicad(String kicad) {
        this.kicad = kicad;
    }

    public Integer getPads() {
        return pads;
    }

    public void setPads(Integer pads) {
        this.pads = pads;
    }

    public Integer getHoles() {
        return holes;
    }

    public void setHoles(Integer holes) {
        this.holes = holes;
    }

    public Integer getNpth() {
        return npth;
    }

    public void setNpth(Integer npth) {
        this.npth = npth;
    }
}
