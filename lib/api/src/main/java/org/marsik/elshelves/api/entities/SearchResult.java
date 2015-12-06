package org.marsik.elshelves.api.entities;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberModelName;

import java.util.Collection;
import java.util.Set;

@Getter
@Setter
@JsonTypeName("search")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "_type",
        visible = true,
        defaultImpl = SearchResult.class)
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
