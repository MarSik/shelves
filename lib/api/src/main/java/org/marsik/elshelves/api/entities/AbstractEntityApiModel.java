package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gnu.trove.map.hash.THashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.marcus.ember.EmberIgnore;
import org.marsik.elshelves.api.ember.EmberEntity;

import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractEntityApiModel implements EmberEntity {
    UUID id;
}
