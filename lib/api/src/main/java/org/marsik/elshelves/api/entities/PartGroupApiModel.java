package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.PartGroupIdResolver;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id", resolver = PartGroupIdResolver.class)
@JsonTypeName("group")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "_type",
        visible = true,
        defaultImpl = PartGroupApiModel.class)
public class PartGroupApiModel extends AbstractNamedEntityApiModel {
	public PartGroupApiModel(UUID id) {
		super(id);
	}

    public PartGroupApiModel(String uuid) {
        super(uuid);
    }

    Set<PartGroupApiModel> groups;
    PartGroupApiModel parent;

    Set<PartTypeApiModel> types;
    Set<NumericPropertyApiModel> showProperties;

	Long directCount;
	Long nestedCount;

    public PartGroupApiModel() {
    }

    public PartGroupApiModel(UUID id, String name) {
        this(id);
        setName(name);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
