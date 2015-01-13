package org.marsik.elshelves.backend.entities;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

@NodeEntity
public class Box extends NamedEntity {

	@RelatedTo(type = "PARENT", direction = Direction.INCOMING)
    Set<Box> contains;

    @RelatedTo(type = "PARENT")
    Box parent;

	@RelatedTo(type = "LOCATED_AT", direction = Direction.INCOMING)
	Set<Lot> lots;

	@PartOfUpdate
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

	@PartOfUpdate
	public Set<Lot> getLots() {
		return lots;
	}

	public void setLots(Set<Lot> lots) {
		this.lots = lots;
	}
}
