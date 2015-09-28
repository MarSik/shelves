package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.marsik.elshelves.api.ember.EmberEntity;

import java.util.UUID;

@Data
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public abstract class AbstractEntityApiModel implements EmberEntity {
    public AbstractEntityApiModel(UUID uuid) {
        this.id = uuid;
    }

    @JsonProperty("id")
    UUID id;
}
