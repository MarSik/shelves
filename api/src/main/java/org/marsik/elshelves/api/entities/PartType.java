package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.api.ember.EmberModelName;

import java.util.List;
import java.util.Map;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@EmberModelName("type")
public class PartType extends AbstractEntity {
    Long id;
    String name;

    Footprint footprint;
    List<PartGroup> groups;

    @Override
    public Map getLinks() {
        Map<String, String> links = new THashMap<>();
        links.put("groups", "groups");
        return links;
    }

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

    @JsonIdentityReference(alwaysAsId = true)
    public Footprint getFootprint() {
        return footprint;
    }

    @JsonIgnore
    public void setFootprint(Footprint footprint) {
        this.footprint = footprint;
    }

    @JsonSetter
    public void setFootprint(Long footprint) {
        this.footprint = new Footprint();
        this.footprint.setId(footprint);
    }

    @JsonIdentityReference(alwaysAsId = true)
    public List<PartGroup> getGroups() {
        return groups;
    }

    @JsonSetter
    public void setGroups(List<PartGroup> groups) {
        this.groups = groups;
    }
}
