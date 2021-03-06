package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.marsik.elshelves.api.entities.idresolvers.ItemIdResolver;
import org.marsik.elshelves.ember.EmberModelName;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id", resolver = ItemIdResolver.class)
@JsonTypeName("list")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "_type",
        visible = true,
        defaultImpl = ListApiModel.class)
public class ListApiModel extends AbstractNamedEntityApiModel {
    public ListApiModel(UUID id) {
        super(id);
    }

    public ListApiModel() {
    }

    public ListApiModel(String uuid) {
        super(uuid);
    }

    Set<AbstractEntityApiModel> items;
}
