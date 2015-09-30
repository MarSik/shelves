package org.marsik.elshelves.api.entities;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.marsik.elshelves.api.ember.EmberModelName;

import java.util.Collection;

@Data
@EqualsAndHashCode(of = {}, callSuper = true)
@EmberModelName("search")
public class SearchResult extends AbstractEntityApiModel {
    String query;
    Collection<PolymorphicRecord> items;
}
