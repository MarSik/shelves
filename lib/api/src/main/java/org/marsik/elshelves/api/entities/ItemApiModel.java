package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.ItemIdResolver;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id", resolver = ItemIdResolver.class)
@JsonTypeName("item")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "_type",
        visible = true,
        defaultImpl = ItemApiModel.class)
public class ItemApiModel extends LotApiModel {
    public ItemApiModel(UUID id) {
        super(id);
    }

    public ItemApiModel() {
    }

    public ItemApiModel(String uuid) {
        super(uuid);
    }

    Set<RequirementApiModel> requirements;
    Boolean finished;

    /**
     * Type only used when new project is started. Can't be changed.
     */
    PartTypeApiModel type;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
