package org.marsik.elshelves.api.entities;

import gnu.trove.map.hash.THashMap;
import nl.marcus.ember.EmberIgnore;
import org.marsik.elshelves.api.ember.EmberEntity;

import java.util.Map;
import java.util.UUID;

public abstract class AbstractEntity implements EmberEntity {
    @Override
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

        AbstractEntity that = (AbstractEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
