package org.marsik.elshelves.api.entities;


import org.marsik.elshelves.api.ember.EmberModelName;

import java.util.Collection;
import java.util.List;

@EmberModelName("search")
public class SearchResult extends AbstractEntityApiModel {
    String query;
    Collection<PolymorphicRecord> items;

    public Collection<PolymorphicRecord> getItems() {
        return items;
    }

    public void setItems(Collection<PolymorphicRecord> items) {
        this.items = items;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
