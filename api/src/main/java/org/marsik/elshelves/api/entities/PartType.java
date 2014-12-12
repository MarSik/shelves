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
@EmberModelName("type")
public class PartType extends AbstractEntity {
    UUID id;
    String name;
    String description;

    Footprint footprint;
    List<PartGroup> groups;

    User belongsTo;

    @Override
    public Map getLinks() {
        Map<String, String> links = new THashMap<String, String>();
        links.put("groups", "groups");
        return links;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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
    public void setFootprint(UUID footprint) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
