package org.marsik.elshelves.backend.entities;

import org.marsik.elshelves.api.entities.BoxApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

@NodeEntity
@DefaultEmberModel(BoxApiModel.class)
public class Box extends NamedEntity implements StickerCapable {

	@RelatedTo(type = "PARENT", direction = Direction.INCOMING)
    Set<Box> contains;

    @RelatedTo(type = "PARENT")
    Box parent;

	@RelatedTo(type = "LOCATED_AT", direction = Direction.INCOMING)
	Set<Lot> lots;

	public Set<Box> getContains() {
        return contains;
    }

    public void setContains(Set<Box> contains) {
        this.contains = contains;
    }

	@PartOfUpdate
    public Box getParent() {
        return parent;
    }

    public void setParent(Box parent) {
        this.parent = parent;
    }

	public Set<Lot> getLots() {
		return lots;
	}

	public void setLots(Set<Lot> lots) {
		this.lots = lots;
	}

	@Override
	public boolean canBeDeleted() {
		return true;
	}

	@Override
	public String getBaseUrl() {
		return "boxes";
	}
}
