package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.PurchasedLot;
import org.marsik.elshelves.backend.entities.Sku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class EmberToPurchase extends AbstractEmberToEntity<PurchaseApiModel, Purchase> {
	@Autowired
	EmberToTransaction emberToTransaction;

	@Autowired
	EmberToLot emberToLot;

	@Autowired
	EmberToType emberToType;

	@Autowired
	EmberToEntityConversionService conversionService;

	public EmberToPurchase() {
		super(Purchase.class);
	}

	@PostConstruct
	void postConstruct() {
		conversionService.register(PurchaseApiModel.class, this);

	}

	@Override
	public Purchase convert(String path, PurchaseApiModel object, Purchase model, Map<UUID, Object> cache, Set<String> include) {
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
		model.setTransaction(emberToTransaction.convert(path, "transaction", object.getTransaction(), cache, include));
		model.setType(emberToType.convert(path, "type", object.getType(), cache, include));

		if (object.getSku() != null) {
			model.setSku(new Sku());
			model.getSku().setSku(object.getSku());
			model.getSku().setType(model.getType());
			model.getSku().setSource(null);
		}

		if (object.getLots() != null) {
			model.setLots(new THashSet<PurchasedLot>());
			for (LotApiModel l: object.getLots()) {
				final PurchasedLot lot = conversionService.converter(l, PurchasedLot.class)
						.convert(path, "lot", l, cache, include);
				model.addLot(lot);
			}
		} else {
			model.setLots(new IdentifiedEntity.UnprovidedSet<>());
		}

		return model;
	}
}
