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
import java.util.UUID;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@EmberModelName("group")
public class PartGroup extends AbstractEntity {
    UUID id;
    String name;

    List<PartGroup> groups;
    PartGroup parent;

    List<PartType> types;

    @Override
    public Map getLinks() {
        Map<String, String> links = new THashMap<>();
        links.put("groups", "groups");
        links.put("types", "types");
        return links;
    }

    public PartGroup() {
    }

    public PartGroup(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public List<PartGroup> getGroups() {
        return groups;
    }

    @JsonSetter
    public void setGroups(List<PartGroup> groups) {
        this.groups = groups;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public PartGroup getParent() {
        return parent;
    }

    @JsonIgnore
    public void setParent(PartGroup parent) {
        this.parent = parent;
    }

    @JsonSetter
    public void setParent(UUID parent) {
        this.parent = new PartGroup();
        this.parent.setId(parent);
    }

    @JsonIdentityReference(alwaysAsId = true)
    public List<PartType> getTypes() {
        return types;
    }

    @JsonSetter
    public void setTypes(List<PartType> types) {
        this.types = types;
    }
}
