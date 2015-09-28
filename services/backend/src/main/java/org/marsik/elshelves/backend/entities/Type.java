package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.entities.fields.PartCount;
import org.marsik.elshelves.backend.services.StickerCapable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@ToString(of = {}, callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@DefaultEmberModel(PartTypeApiModel.class)
public class Type extends NamedEntity implements StickerCapable {
	String vendor;
	String customId;

    Long minimumCount;
    Long buyMultiple;

    // Should serial numbers be tracked?
    Boolean serials = false;

	/**
	 * Enable manufacturability of this type, When this is set to true
	 * it will allow tracking of separate items with requirements, sub-components'
	 * placement and solder status and so no
	 */
	Boolean manufacturable = false;

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Set<Footprint> footprints;

	@ManyToMany(mappedBy = "types",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Set<Group> groups;

	@OneToMany(mappedBy = "type",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Collection<Purchase> purchases;

	@ManyToMany(mappedBy = "type",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Collection<Requirement> usedIn;

	@JoinTable(name = "type_type_see_also",
			joinColumns = {
					@JoinColumn(name = "type1", nullable = false)},
			inverseJoinColumns = {
					@JoinColumn(name = "type2", nullable = false)})
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
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
