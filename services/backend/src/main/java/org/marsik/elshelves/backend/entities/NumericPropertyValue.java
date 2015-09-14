package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@RelationshipEntity(type = "HAS_PROPERTY")
public class NumericPropertyValue {
    @NotNull
    @StartNode
    NamedEntity entity;

    @NotNull
    @EndNode
    NumericProperty property;

    @NotNull
    @Indexed
    Long value;
}
