package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Collection;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@DefaultEmberModel(DocumentApiModel.class)
public class Document extends NamedEntity implements StickerCapable {
	@NotEmpty
	String contentType;
	@NotNull
	Long size;

	@NotNull
	@CreatedDate
	DateTime created;

    URL url;

	@ManyToOne(targetEntity = NamedEntity.class,
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Collection<NamedEntity> describes;

	@PartOfUpdate
	public String getContentType() {
		return contentType;
	}

	@PartOfUpdate
	public Long getSize() {
		return size;
	}

	@PartOfUpdate
	public DateTime getCreated() {
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
