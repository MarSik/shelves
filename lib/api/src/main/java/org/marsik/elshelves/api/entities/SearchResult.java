package org.marsik.elshelves.api.entities;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberModelName;

import java.util.Collection;
import java.util.Set;

@Getter
@Setter
@EmberModelName("search")
public class SearchResult extends AbstractEntityApiModel {
    String query;
    Collection<AbstractNamedEntityApiModel> items;
    Set<String> include;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
