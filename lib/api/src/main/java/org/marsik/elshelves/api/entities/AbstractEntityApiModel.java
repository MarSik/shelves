package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gnu.trove.map.hash.THashMap;
import nl.marcus.ember.EmberIgnore;
import org.marsik.elshelves.api.ember.EmberEntity;

import java.util.Map;
import java.util.UUID;

public abstract class AbstractEntityApiModel implements EmberEntity {
	protected AbstractEntityApiModel(UUID id) {
		this.id = id;
	}

	protected AbstractEntityApiModel() {
	}

	@Override
	@JsonIgnore
    @EmberIgnore
    public Map<String, String> getLinks() {
        return new THashMap<String, String>(0);
    }

    UUID id;

    @Override
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractEntityApiModel that = (AbstractEntityApiModel) o;

        if (id == null || that.id == null) return false;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
