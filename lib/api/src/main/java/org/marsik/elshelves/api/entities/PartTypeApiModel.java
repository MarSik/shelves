package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.PartTypeIdResolver;

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
	String customId;

    Long minimumCount;
    Long buyMultiple;

    Set<FootprintApiModel> footprints;
	Set<PartGroupApiModel> groups;
	Set<LotApiModel> lots;
    Set<PartTypeApiModel> seeAlso;

    Boolean serials;

    // Parts in store
    Long available;
    // Parts in store and unassigned
    Long free;
    // Parts in store and on boards
    Long total;

    @JsonIdentityReference(alwaysAsId = true)
    public Set<FootprintApiModel> getFootprints() {
        return footprints;
    }

    @JsonSetter
    public void setFootprints(Set<FootprintApiModel> footprints) {
        this.footprints = footprints;
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

	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public Set<LotApiModel> getLots() {
		return lots;
	}

	@JsonSetter
	public void setLots(Set<LotApiModel> lots) {
		this.lots = lots;
	}

    public Long getAvailable() {
        return available;
    }

    public void setAvailable(Long available) {
        this.available = available;
    }

    public Long getFree() {
        return free;
    }

    public void setFree(Long free) {
        this.free = free;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Boolean getSerials() {
        return serials;
    }

    public void setSerials(Boolean serials) {
        this.serials = serials;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public Set<PartTypeApiModel> getSeeAlso() {
        return seeAlso;
    }

    public void setSeeAlso(Set<PartTypeApiModel> seeAlso) {
        this.seeAlso = seeAlso;
    }

    public Long getMinimumCount() {
        return minimumCount;
    }

    public void setMinimumCount(Long minimumCount) {
        this.minimumCount = minimumCount;
    }

    public Long getBuyMultiple() {
        return buyMultiple;
    }

    public void setBuyMultiple(Long buyMultiple) {
        this.buyMultiple = buyMultiple;
    }
}
