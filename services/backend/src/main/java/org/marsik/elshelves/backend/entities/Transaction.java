package org.marsik.elshelves.backend.entities;

import org.marsik.elshelves.backend.services.StickerCapable;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Date;
import java.util.Set;

@NodeEntity
public class Transaction extends NamedEntity implements StickerCapable {
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

	@Override
	public boolean canBeDeleted() {
		for (Purchase p: getItems()) {
			if (!p.canBeDeleted()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String getBaseUrl() {
		return "transactions";
	}
}
