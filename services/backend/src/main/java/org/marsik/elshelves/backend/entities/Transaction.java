package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.TransactionApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.services.StickerCapable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString(of = {}, callSuper = true)
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@DefaultEmberModel(TransactionApiModel.class)
public class Transaction extends NamedEntity implements StickerCapable {
	@org.hibernate.annotations.Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	DateTime date;

	@OneToMany(mappedBy = "transaction",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Set<Purchase> items = new THashSet<>();

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE },
			optional = false)
	Source source;

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
}
