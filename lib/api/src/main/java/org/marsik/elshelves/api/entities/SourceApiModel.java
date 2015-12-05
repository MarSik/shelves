package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.SourceIdResolver;

import java.util.UUID;

@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id", resolver = SourceIdResolver.class)
@EmberModelName("source")
public class SourceApiModel extends AbstractNamedEntityApiModel {
	public SourceApiModel(UUID id) {
		super(id);
	}

	public SourceApiModel() {
	}

	public SourceApiModel(String uuid) {
		super(uuid);
	}

	String url;
	String skuUrl;

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
