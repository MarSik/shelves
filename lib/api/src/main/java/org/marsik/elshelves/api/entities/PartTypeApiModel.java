package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.PartTypeIdResolver;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = {}, callSuper = true)
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
    Boolean manufacturable;

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

    @JsonIdentityReference(alwaysAsId = true)
    public Set<PartGroupApiModel> getGroups() {
        return groups;
    }

	@JsonIdentityReference(alwaysAsId = true)
	public Set<LotApiModel> getLots() {
		return lots;
	}

    @JsonIdentityReference(alwaysAsId = true)
    public Set<PartTypeApiModel> getSeeAlso() {
        return seeAlso;
    }
}
