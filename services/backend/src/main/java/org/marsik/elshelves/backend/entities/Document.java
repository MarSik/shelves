package org.marsik.elshelves.backend.entities;

import org.hibernate.validator.constraints.NotEmpty;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@NodeEntity
public class Document extends NamedEntity implements StickerCapable {
	@NotEmpty
	String contentType;
	@NotNull
	Long size;

	@NotNull
	Date created;

	@RelatedTo(type = "DESCRIBES")
	Set<NamedEntity> describes;

	@PartOfUpdate
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@PartOfUpdate
	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	@PartOfUpdate
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public boolean canBeDeleted() {
		return true;
	}

	@Override
	public String getBaseUrl() {
		return "/documents";
	}
}
