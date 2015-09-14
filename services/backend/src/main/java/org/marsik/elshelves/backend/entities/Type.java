package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.entities.fields.PartCount;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@NodeEntity
@DefaultEmberModel(PartTypeApiModel.class)
public class Type extends NamedEntity implements StickerCapable {
	String vendor;
	String customId;

    Long minimumCount;
    Long buyMultiple;

    // Should serial numbers be tracked?
    Boolean serials;

	@NotNull
	@RelatedTo(type = "HAS_FOOTPRINT")
	Set<Footprint> footprints;

	@RelatedTo(type = "CONTAINS", direction = Direction.INCOMING)
	Set<Group> groups;

	@RelatedTo(type = "OF_TYPE", direction = Direction.INCOMING)
	Iterable<Purchase> purchases;

	@RelatedTo(type = "REQUIRED_TYPE", direction = Direction.INCOMING)
	Iterable<Requirement> usedIn;

    @RelatedTo(type = "SEE_ALSO", direction = Direction.BOTH)
    Set<Type> seeAlso;

	@PartOfUpdate
	public String getVendor() {
		return vendor;
	}

	@PartOfUpdate
	public String getCustomId() {
		return customId;
	}

	@PartOfUpdate
	public Set<Footprint> getFootprints() {
		return footprints;
	}

	@PartOfUpdate
	public Set<Group> getGroups() {
		return groups;
	}

	public Iterable<Lot> getLots() {
		List<Lot> lots = new ArrayList<>();
		for (Purchase p: getPurchases()) {
			for (Lot l: p.getLots()) {
				lots.add(l);
			}
		}

		return lots;
	}

	@Override
	public boolean canBeDeleted() {
		return !(getPurchases().iterator().hasNext() || getUsedIn().iterator().hasNext());
	}

    public PartCount getAvailable() {
        PartCount count = new PartCount();
        for (Lot l: getLots()) {
            if (l.isCanBeAssigned()) {
                count.available += l.getCount();
                count.free += l.getCount();
                count.total += l.getCount();
            } else if (l.isCanBeSoldered()) {
                count.available += l.getCount();
                count.total += l.getCount();
            } else if (l.isCanBeUnsoldered()) {
                count.total += l.getCount();
            }
        }

        return count;
    }

	@Override
	public String getBaseUrl() {
		return "types";
	}

    @PartOfUpdate
    public Boolean getSerials() {
        return serials;
    }

    @PartOfUpdate
    public Set<Type> getSeeAlso() {
        return seeAlso;
    }

    @PartOfUpdate
    public Long getMinimumCount() {
        return minimumCount;
    }

    @PartOfUpdate
    public Long getBuyMultiple() {
        return buyMultiple;
    }
}
