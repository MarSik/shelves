package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
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

	@NotEmpty
	@ManyToMany(fetch = FetchType.LAZY)
	Set<NamedEntity> describes = new THashSet<>();

	public void addDescribes(NamedEntity n) {
		describes.add(n);
		n.getDescribedBy().add(this);
	}

	public void removeDescribes(NamedEntity n) {
		describes.remove(n);
		n.getDescribedBy().remove(this);
	}

	@Override
	public boolean canBeDeleted() {
		return true;
	}

	@Override
	public String getBaseUrl() {
		return "/documents";
	}

	@Override
	public void updateFrom(UpdateableEntity update0) throws OperationNotPermitted {
		if (!(update0 instanceof Document)) {
			throw new IllegalArgumentException();
		}

		Document update = (Document)update0;

		update(update.getContentType(), this::setContentType);
		update(update.getSize(), this::setSize);
		update(update.getUrl(), this::setUrl);

		reconcileLists(update.getDescribes(), this::getDescribes, this::addDescribes, this::removeDescribes);

		super.updateFrom(update0);
	}

	@Override
	public void relink(Relinker relinker) {
		relinkList(relinker, this::getDescribes, this::addDescribes, this::removeDescribes);
		super.relink(relinker);
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
