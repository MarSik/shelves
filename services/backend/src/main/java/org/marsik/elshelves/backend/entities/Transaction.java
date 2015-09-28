package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.TransactionApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString(of = {}, callSuper = true)
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@DefaultEmberModel(TransactionApiModel.class)
@EntityListeners({AuditingEntityListener.class})
public class Transaction extends NamedEntity implements StickerCapable {
	@org.hibernate.annotations.Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	DateTime date;

	@OneToMany(mappedBy = "transaction",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Set<Purchase> items;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
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
}
