package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import gnu.trove.map.hash.THashMap;
import nl.marcus.ember.EmberIgnore;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.ember.Sideload;
import org.marsik.elshelves.api.entities.idresolvers.PartGroupIdResolver;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = PartGroupIdResolver.class)
@EmberModelName("group")
public class PartGroupApiModel extends AbstractNamedEntityApiModel {
	public PartGroupApiModel(UUID id) {
		super(id);
	}

	Set<PartGroupApiModel> groups;
	@Sideload
    PartGroupApiModel parent;

    Set<PartTypeApiModel> types;

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
        setName(name);
    }

    @JsonIdentityReference(alwaysAsId = true)
    public Set<PartGroupApiModel> getGroups() {
        return groups;
    }

    @JsonSetter
    public void setGroups(Set<PartGroupApiModel> groups) {
        this.groups = groups;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public PartGroupApiModel getParent() {
        return parent;
    }

    @JsonSetter
    public void setParent(PartGroupApiModel parent) {
        this.parent = parent;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public Set<PartTypeApiModel> getTypes() {
        return types;
    }

    @JsonSetter
    public void setTypes(Set<PartTypeApiModel> types) {
        this.types = types;
    }
}
