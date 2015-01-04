package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.SourceIdResolver;

import java.util.UUID;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = SourceIdResolver.class)
@EmberModelName("source")
public class SourceApiModel extends AbstractNamedEntityApiModel {
	public SourceApiModel(UUID id) {
		super(id);
	}

	public SourceApiModel() {
	}

	String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
