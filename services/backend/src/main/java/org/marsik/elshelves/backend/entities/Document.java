package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@NodeEntity
@DefaultEmberModel(DocumentApiModel.class)
public class Document extends NamedEntity implements StickerCapable {
	@NotEmpty
	String contentType;
	@NotNull
	Long size;

	@NotNull
	Date created;

    URL url;

	@RelatedTo(type = "DESCRIBES")
	Set<NamedEntity> describes;

	@PartOfUpdate
	public String getContentType() {
		return contentType;
	}

	@PartOfUpdate
	public Long getSize() {
		return size;
	}

	@PartOfUpdate
	public Date getCreated() {
		return created;
	}

	@Override
	public boolean canBeDeleted() {
		return true;
	}

	@Override
	public String getBaseUrl() {
		return "/documents";
	}

    @PartOfUpdate
    public URL getUrl() {
        return url;
    }
}
