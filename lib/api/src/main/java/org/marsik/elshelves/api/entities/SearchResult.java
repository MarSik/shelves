package org.marsik.elshelves.api.entities;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberModelName;

import java.util.Collection;

@Getter
@Setter
@EmberModelName("search")
public class SearchResult extends AbstractEntityApiModel {
    String query;
    Collection<PolymorphicRecord> items;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
