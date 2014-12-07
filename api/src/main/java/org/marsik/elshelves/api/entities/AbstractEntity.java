package org.marsik.elshelves.api.entities;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.api.ember.EmberEntity;

import java.util.Map;

public abstract class AbstractEntity implements EmberEntity {
    @Override
    public Map getLinks() {
        return new THashMap(0);
    }
}
