package org.marsik.elshelves.backend.entities;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;
import java.util.UUID;

@NodeEntity
public class Box implements OwnedEntity {
    @RelatedTo(type = "OWNS", direction = Direction.INCOMING, enforceTargetType = true)
    User owner;

    @Indexed
    UUID uuid;
    String name;

    @RelatedTo(type = "PARENT", direction = Direction.INCOMING)
    Set<Box> contains;

    @RelatedTo(type = "PARENT")
    Box parent;

	@RelatedTo(type = "LOCATED_AT", direction = Direction.INCOMING)
	Set<Lot> lots;

    @Override
    public User getOwner() {
        return owner;
    }

    @Override
    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Box> getContains() {
        return contains;
    }

    public void setContains(Set<Box> contains) {
        this.contains = contains;
    }

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
}
