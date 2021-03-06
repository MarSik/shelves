package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.marsik.elshelves.api.fields.SkuLink;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.PartTypeIdResolver;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id", resolver = PartTypeIdResolver.class)
@JsonTypeName("type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "_type",
        visible = true,
        defaultImpl = PartTypeApiModel.class)
public class PartTypeApiModel extends AbstractNamedEntityApiModel {
	public PartTypeApiModel(UUID id) {
		super(id);
	}

	public PartTypeApiModel() {
	}

    public PartTypeApiModel(String uuid) {
        super(uuid);
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

    Set<UUID> skus;
    Map<UUID, SkuLink> skuValues;

    Boolean serials;
    Boolean manufacturable;

    // Parts in store
    Long available;
    // Parts in store and unassigned
    Long free;
    // Parts in store and on boards
    Long total;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
