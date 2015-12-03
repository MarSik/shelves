package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.marsik.elshelves.api.entities.idresolvers.ItemIdResolver;
import org.marsik.elshelves.ember.EmberModelName;

import java.util.Set;

@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id", resolver = ItemIdResolver.class)
@EmberModelName("list")
public class ListApiModel extends AbstractNamedEntityApiModel {
    Set<AbstractEntityApiModel> items;
}
