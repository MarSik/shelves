package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Collection;

@Data
@NoArgsConstructor
@ToString(of = {}, callSuper = true)
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
	@org.hibernate.annotations.Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	DateTime created;

    URL url;

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Collection<NamedEntity> describes = new THashSet<>();

	@Override
	public boolean canBeDeleted() {
		return true;
	}

	@Override
	public String getBaseUrl() {
		return "/documents";
	}

	@Override
	public void updateFrom(UpdateableEntity update0) {
		if (!(update0 instanceof Document)) {
			throw new IllegalArgumentException();
		}

		Document update = (Document)update0;

		update(update.getContentType(), this::setContentType);
		update(update.getSize(), this::setSize);
		update(update.getUrl(), this::setUrl);

		super.updateFrom(update0);
	}
}
