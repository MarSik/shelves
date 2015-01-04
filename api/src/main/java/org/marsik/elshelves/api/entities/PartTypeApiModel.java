package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import nl.marcus.ember.EmberIgnore;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.ember.Sideload;
import org.marsik.elshelves.api.entities.idresolvers.PartTypeIdResolver;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = PartTypeIdResolver.class)
@EmberModelName("type")
public class PartTypeApiModel extends AbstractNamedEntityApiModel {
	public PartTypeApiModel(UUID id) {
		super(id);
	}

	public PartTypeApiModel() {
	}

	String description;

	String vendor;
	String vendorId;

	@Sideload
    FootprintApiModel footprint;
	Set<PartGroupApiModel> groups;
	Set<LotApiModel> lots;

    @Override
    @EmberIgnore
    public Map<String, String> getLinks() {
        Map<String, String> links = super.getLinks();
        links.put("groups", "groups");
		links.put("lots", "lots");
        return links;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public FootprintApiModel getFootprint() {
        return footprint;
    }

    @JsonSetter
    public void setFootprint(FootprintApiModel footprint) {
        this.footprint = footprint;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public Set<PartGroupApiModel> getGroups() {
        return groups;
    }

	@JsonSetter
	public void setGroups(Set<PartGroupApiModel> groups) {
		this.groups = groups;
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
	public Set<LotApiModel> getLots() {
		return lots;
	}

	@JsonSetter
	public void setLots(Set<LotApiModel> lots) {
		this.lots = lots;
	}
}
