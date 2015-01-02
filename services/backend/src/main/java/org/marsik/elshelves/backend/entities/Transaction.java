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
public class Transaction extends NamedObject {
	Date date;

	@RelatedTo(type = "IN_TRANSACTION", direction = Direction.INCOMING)
	Set<Purchase> items;

	@RelatedTo(type = "PURCHASED_FROM")
	Source source;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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
