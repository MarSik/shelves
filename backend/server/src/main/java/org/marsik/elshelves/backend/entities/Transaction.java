package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.TransactionApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.marsik.elshelves.backend.services.StickerCapable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DefaultEmberModel(TransactionApiModel.class)
public class Transaction extends NamedEntity implements StickerCapable {
	@org.hibernate.annotations.Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	DateTime date;

	@OneToMany(mappedBy = "transaction",
			cascade = { CascadeType.ALL },
			orphanRemoval = true)
	Set<Purchase> items = new THashSet<>();

	public void addItem(Purchase p) {
		p.setTransaction(this);
	}

	public void removeItem(Purchase p) {
		p.unsetTransaction(this);
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE },
			optional = false)
	Source source;

	public void setSource(Source s) {
		if (source != null) source.getTransactions().remove(this);
		source = s;
		if (source != null) source.getTransactions().add(this);
	}

	public void unsetSource(Source s) {
		assert s.equals(source);
		setSource(null);
	}

	@Override
	public boolean canBeDeleted() {
		for (Purchase p: getItems()) {
			if (!p.canBeDeleted()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String getBaseUrl() {
		return "transactions";
	}

	@Override
	public void updateFrom(UpdateableEntity update0) {
		if (!(update0 instanceof Transaction)) {
			throw new IllegalArgumentException();
		}

		Transaction update = (Transaction)update0;

		update(update.getDate(), this::setDate);
		update(update.getSource(), this::setSource);


		super.updateFrom(update0);
	}

	@Override
	public void relink(Relinker relinker) {
		relinkItem(relinker, getSource(), this::setSource);
		relinkList(relinker, this::getItems, this::addItem, this::removeItem);
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
