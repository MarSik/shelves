package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.marsik.elshelves.api.entities.SourceApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.entities.fields.ShippingCalculator;
import org.marsik.elshelves.backend.entities.fields.SourceDownloader;
import org.marsik.elshelves.backend.interfaces.Relinker;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString(of = {}, callSuper = true)
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@DefaultEmberModel(SourceApiModel.class)
public class Source extends NamedEntity {
	String url;

	@NotNull
	@Enumerated(EnumType.STRING)
	SourceDownloader sourceDownloader = SourceDownloader.NONE;

	@NotNull
	@Enumerated(EnumType.STRING)
	ShippingCalculator shippingCalculator = ShippingCalculator.NONE;

	@OneToMany(mappedBy = "source", fetch = FetchType.LAZY,
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Set<Transaction> transactions = new THashSet<>();

	public void addTransaction(Transaction t) {
		t.setSource(this);
	}

	public void removeTransaction(Transaction t) {
		t.unsetSource(this);
	}

	@Transient
	private boolean hasIcon;

	@PartOfUpdate
	public String getUrl() {
		return url;
	}

	public boolean canBeDeleted() {
		return !getTransactions().iterator().hasNext();
	}

	@Override
	public void updateFrom(UpdateableEntity update0) {
		if (!(update0 instanceof Source)) {
			throw new IllegalArgumentException();
		}

		Source update = (Source)update0;

		update(update.getUrl(), this::setUrl);
		update(update.getSourceDownloader(), this::setSourceDownloader);
		update(update.getShippingCalculator(), this::setShippingCalculator);

		super.updateFrom(update0);
	}

	@Override
	public void relink(Relinker relinker) {
		relinkList(relinker, this::getTransactions, this::addTransaction, this::removeTransaction);
		super.relink(relinker);
	}
}
