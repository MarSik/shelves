package org.marsik.elshelves.backend.entities;

import org.marsik.elshelves.api.entities.SourceApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.entities.fields.ShippingCalculator;
import org.marsik.elshelves.backend.entities.fields.SourceDownloader;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@NodeEntity
@DefaultEmberModel(SourceApiModel.class)
public class Source extends NamedEntity {
	String url;

	SourceDownloader sourceDownloader;
	ShippingCalculator shippingCalculator;

	@RelatedTo(type = "PURCHASED_FROM", direction = Direction.INCOMING)
	Iterable<Purchase> purchases;

	@PartOfUpdate
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
