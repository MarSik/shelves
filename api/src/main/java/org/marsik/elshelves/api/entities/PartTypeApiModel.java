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
import org.marsik.elshelves.api.entities.deserializers.EmberIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.FootprintIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.LotIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.PartGroupIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.UserIdDeserializer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@EmberModelName("type")
public class PartTypeApiModel extends AbstractEntityApiModel {
    String name;
    String description;

	String vendor;
	String vendorId;

    FootprintApiModel footprint;
	List<PartGroupApiModel> groups;
	List<LotApiModel> lots;

    UserApiModel belongsTo;

    @Override
    @EmberIgnore
    public Map<String, String> getLinks() {
        Map<String, String> links = new THashMap<String, String>();
        links.put("groups", "groups");
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
    public FootprintApiModel getFootprint() {
        return footprint;
    }

    @JsonSetter
	@JsonDeserialize(using = FootprintIdDeserializer.class)
    public void setFootprint(FootprintApiModel footprint) {
        this.footprint = footprint;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public List<PartGroupApiModel> getGroups() {
        return groups;
    }

	@JsonSetter
	@JsonDeserialize(contentUsing = PartGroupIdDeserializer.class)
	public void setGroups(List<PartGroupApiModel> groups) {
		this.groups = groups;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public List<LotApiModel> getLots() {
		return lots;
	}

	@JsonSetter
	@JsonDeserialize(contentUsing = LotIdDeserializer.class)
	public void setLots(List<LotApiModel> lots) {
		this.lots = lots;
	}
}
