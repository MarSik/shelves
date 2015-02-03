package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.idresolvers.DocumentIdResolver;

import java.net.URL;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

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
	Date created;
    URL url;

	UserApiModel belongsTo;
	Set<PolymorphicRecord> describes;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    @JsonIdentityReference(alwaysAsId = true)
	public UserApiModel getBelongsTo() {
		return belongsTo;
	}

	@JsonSetter
	public void setBelongsTo(UserApiModel belongsTo) {
		this.belongsTo = belongsTo;
	}

	public static class PolymorphicRecord extends AbstractEntityApiModel {
		String type;

		public PolymorphicRecord() {
		}

		public PolymorphicRecord(UUID id) {
			super(id);
		}

		public PolymorphicRecord(String id) {
			super(UUID.fromString(id));
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}

	public Set<PolymorphicRecord> getDescribes() {
		return describes;
	}

	public void setDescribes(Set<PolymorphicRecord> describes) {
		this.describes = describes;
	}
}
