package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"uuid"})
@NodeEntity
public abstract class OwnedEntity {
	@PartOfUpdate
	@RelatedTo(type = "OWNS", direction = Direction.INCOMING, enforceTargetType = true)
	@NotNull
	User owner;

	@NotNull
	@Indexed
	UUID uuid;

    /**
     * Record the timestamp of last modification so
     * we can tell the client to use cached result
     */
    Date lastModified;

	// Provided by AspectJ-ized NodeEntity
	// public abstract Long getNodeId();

	@PartOfUpdate
	public User getOwner() {
		return owner;
	}

	public abstract boolean canBeDeleted();
}
