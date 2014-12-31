package org.marsik.elshelves.backend.entities;

import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.User;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@NodeEntity
public class Transaction implements OwnedEntity {
	@Indexed
	UUID uuid;

	@Indexed
	String name;
	Date date;

	@RelatedTo(type = "OWNS", direction = Direction.INCOMING, enforceTargetType = true)
	User owner;

	@RelatedTo(type = "IN_TRANSACTION", direction = Direction.INCOMING)
	Set<Purchase> items;

	@RelatedTo(type = "PURCHASED_FROM")
	Source source;

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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public User getOwner() {
		return owner;
	}

	@Override
	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Set<Purchase> getItems() {
		return items;
	}

	public void setItems(Set<Purchase> items) {
		this.items = items;
	}
}
