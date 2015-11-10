package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.PolymorphicRecord;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Sku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToPurchase extends AbstractEmberToEntity<PurchaseApiModel, Purchase> {
	@Autowired
	EmberToTransaction emberToTransaction;

	@Autowired
	EmberToLot emberToLot;

	@Autowired
	EmberToType emberToType;

	public EmberToPurchase() {
		super(Purchase.class);
	}

	@Override
	public Purchase convert(PurchaseApiModel object, Purchase model, int nested, Map<UUID, Object> cache) {
		model.setId(object.getId());
		model.setCount(object.getCount());

		if (object.getCurrency() != null) {
			CurrencyUnit currency = CurrencyUnit.of(object.getCurrency());
			if (object.getSinglePrice() != null)
				model.setSinglePrice(BigMoney.of(currency, object.getSinglePrice()));
			if (object.getTotalPrice() != null)
				model.setTotalPrice(BigMoney.of(currency, object.getTotalPrice()));
		}

		if (object.getCurrencyPaid() != null) {
			CurrencyUnit currencyPaid = CurrencyUnit.of(object.getCurrencyPaid());
			if (object.getSinglePricePaid() != null)
				model.setSinglePricePaid(BigMoney.of(currencyPaid, object.getSinglePricePaid()));
			if (object.getTotalPricePaid() != null)
				model.setTotalPricePaid(BigMoney.of(currencyPaid, object.getTotalPricePaid()));
		}

		model.setVat(object.getVat());
		model.setVatIncluded(object.getVatIncluded());
		model.setTransaction(emberToTransaction.convert(object.getTransaction(), nested, cache));
		model.setType(emberToType.convert(object.getType(), nested, cache));

		if (object.getSku() != null) {
			model.setSku(new Sku());
			model.getSku().setSku(object.getSku());
			model.getSku().setType(model.getType());
			model.getSku().setSource(null);
		}

		if (object.getLots() != null) {
			model.setLots(new THashSet<Lot>());
			for (PolymorphicRecord l: object.getLots()) {
				model.addLot(new Lot(l.getId()));
			}
		} else {
			model.setLots(new IdentifiedEntity.UnprovidedSet<>());
		}

		return model;
	}
}
