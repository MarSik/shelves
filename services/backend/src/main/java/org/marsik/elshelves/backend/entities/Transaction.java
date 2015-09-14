package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.marsik.elshelves.api.entities.TransactionApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@NodeEntity
@DefaultEmberModel(TransactionApiModel.class)
public class Transaction extends NamedEntity implements StickerCapable {
	Date date;

	@RelatedTo(type = "IN_TRANSACTION", direction = Direction.INCOMING)
	Set<Purchase> items;

	@RelatedTo(type = "PURCHASED_FROM")
	Source source;

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
