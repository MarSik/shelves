package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id", resolver = DocumentIdResolver.class)
@JsonTypeName("document")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "_type",
		visible = true,
		defaultImpl = DocumentApiModel.class)
public class DocumentApiModel extends AbstractNamedEntityApiModel {
	public DocumentApiModel(UUID id) {
		super(id);
	}

	public DocumentApiModel() {
	}

	public DocumentApiModel(String uuid) {
		super(uuid);
	}

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
