package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.ember.Sideload;
import org.marsik.elshelves.api.entities.deserializers.DocumentIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.UserIdDeserializer;

import java.util.Set;
import java.util.UUID;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@EmberModelName("footprint")
public class FootprintApiModel extends AbstractNamedEntityApiModel {
    String kicad;

    Integer pads;
    Integer holes;
    Integer npth;

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
