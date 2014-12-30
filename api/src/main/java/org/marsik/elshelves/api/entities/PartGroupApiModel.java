package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import gnu.trove.map.hash.THashMap;
import nl.marcus.ember.EmberIgnore;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.deserializers.PartGroupIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.PartTypeIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.UserIdDeserializer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@EmberModelName("group")
public class PartGroupApiModel extends AbstractEntityApiModel {
    String name;

    List<PartGroupApiModel> groups;
    PartGroupApiModel parent;

    List<PartTypeApiModel> types;

    UserApiModel belongsTo;

    @Override
    @EmberIgnore
    public Map<String, String> getLinks() {
        Map<String, String> links = new THashMap<String, String>();
        links.put("groups", "groups");
        links.put("types", "types");
        return links;
    }

    public PartGroupApiModel() {
    }

    public PartGroupApiModel(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public List<PartGroupApiModel> getGroups() {
        return groups;
    }

    @JsonSetter
    public void setGroups(List<PartGroupApiModel> groups) {
        this.groups = groups;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public PartGroupApiModel getParent() {
        return parent;
    }

    @JsonSetter
	@JsonDeserialize(using = PartGroupIdDeserializer.class)
    public void setParent(PartGroupApiModel parent) {
        this.parent = parent;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public List<PartTypeApiModel> getTypes() {
        return types;
    }

    @JsonSetter
	@JsonDeserialize(contentUsing = PartTypeIdDeserializer.class)
    public void setTypes(List<PartTypeApiModel> types) {
        this.types = types;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public UserApiModel getBelongsTo() {
        return belongsTo;
    }

    @JsonSetter
	@JsonDeserialize(using = UserIdDeserializer.class)
    public void setBelongsTo(UserApiModel belongsTo) {
        this.belongsTo = belongsTo;
    }
}
