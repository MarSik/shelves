package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.DocumentIdResolver;

import java.net.URL;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = DocumentIdResolver.class)
@EmberModelName("document")
public class DocumentApiModel extends AbstractEntityApiModel {
	public DocumentApiModel(UUID id) {
		super(id);
	}

	public DocumentApiModel() {
	}

	String name;
	String contentType;
	Long size;
	DateTime created;
    URL url;

	UserApiModel belongsTo;
	Set<AbstractNamedEntityApiModel> describes;

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
