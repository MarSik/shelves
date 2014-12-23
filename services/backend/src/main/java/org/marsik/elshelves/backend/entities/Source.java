package org.marsik.elshelves.backend.entities;

import org.marsik.elshelves.backend.entities.fields.ShippingCalculator;
import org.marsik.elshelves.backend.entities.fields.SourceDownloader;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.UUID;

@NodeEntity
public class Source implements OwnedEntity {
	@Indexed
	UUID uuid;

	@RelatedTo(type = "OWNS", direction = Direction.INCOMING, enforceTargetType = true)
	User owner;

	String name;
	String url;

	SourceDownloader sourceDownloader;
	ShippingCalculator shippingCalculator;

	@RelatedTo(type = "PURCHASED_FROM", direction = Direction.INCOMING)
	Iterable<Purchase> purchases;

	@Override
	public UUID getUuid() {
		return uuid;
	}

	@Override
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public User getOwner() {
		return owner;
	}

	@Override
	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public SourceDownloader getSourceDownloader() {
		return sourceDownloader;
	}

	public void setSourceDownloader(SourceDownloader sourceDownloader) {
		this.sourceDownloader = sourceDownloader;
	}

	public ShippingCalculator getShippingCalculator() {
		return shippingCalculator;
	}

	public void setShippingCalculator(ShippingCalculator shippingCalculator) {
		this.shippingCalculator = shippingCalculator;
	}

	public Iterable<Purchase> getPurchases() {
		return purchases;
	}

	public boolean canBeDeleted() {
		return !getPurchases().iterator().hasNext();
	}
}
