package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.marsik.elshelves.api.entities.SourceApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.entities.fields.ShippingCalculator;
import org.marsik.elshelves.backend.entities.fields.SourceDownloader;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
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

	public boolean canBeDeleted() {
		return !getPurchases().iterator().hasNext();
	}
}
