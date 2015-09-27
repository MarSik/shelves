package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.ItemIdResolver;

import java.util.Set;

@Data
@EqualsAndHashCode(of = {}, callSuper = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = ItemIdResolver.class)
@EmberModelName("item")
public class ItemApiModel extends LotApiModel {
    Set<RequirementApiModel> requirements;
    Boolean finished;

    @JsonIdentityReference(alwaysAsId = true)
    public Set<RequirementApiModel> getRequirements() {
        return requirements;
    }
}
