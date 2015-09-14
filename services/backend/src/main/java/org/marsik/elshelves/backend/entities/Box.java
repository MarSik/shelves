package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.marsik.elshelves.api.entities.BoxApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.persistence.Entity;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@DefaultEmberModel(BoxApiModel.class)
public class Box extends NamedEntity implements StickerCapable {

	@RelatedTo(type = "PARENT", direction = Direction.INCOMING)
    Set<Box> contains;

    @RelatedTo(type = "PARENT")
    Box parent;

	@RelatedTo(type = "LOCATED_AT", direction = Direction.INCOMING)
	Set<Lot> lots;

	@PartOfUpdate
    public Box getParent() {
        return parent;
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
