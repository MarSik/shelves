package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.marsik.elshelves.api.entities.SourceApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.entities.fields.ShippingCalculator;
import org.marsik.elshelves.backend.entities.fields.SourceDownloader;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Collection;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@DefaultEmberModel(SourceApiModel.class)
public class Source extends NamedEntity {
	String url;

	@Enumerated(EnumType.STRING)
	SourceDownloader sourceDownloader;

	@Enumerated(EnumType.STRING)
	ShippingCalculator shippingCalculator;

	@OneToMany(mappedBy = "source", fetch = FetchType.LAZY,
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Collection<Transaction> transactions;

	@PartOfUpdate
	public String getUrl() {
		return url;
	}

	public boolean canBeDeleted() {
		return !getTransactions().iterator().hasNext();
	}
}
