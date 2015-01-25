package org.marsik.elshelves.backend.entities;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import javax.validation.constraints.NotNull;

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

    public NamedEntity getEntity() {
        return entity;
    }

    public void setEntity(NamedEntity entity) {
        this.entity = entity;
    }

    public NumericProperty getProperty() {
        return property;
    }

    public void setProperty(NumericProperty property) {
        this.property = property;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
