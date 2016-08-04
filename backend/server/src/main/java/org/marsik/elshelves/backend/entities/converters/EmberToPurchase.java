package org.marsik.elshelves.backend.entities.converters;

import org.javamoney.moneta.Money;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Sku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
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
			CurrencyUnit currency = Monetary.getCurrency(object.getCurrency());
			if (object.getSinglePrice() != null)
				model.setSinglePrice(Money.of(object.getSinglePrice(), currency));
			if (object.getTotalPrice() != null)
				model.setTotalPrice(Money.of(object.getTotalPrice(), currency));
		}

		if (object.getCurrencyPaid() != null) {
			CurrencyUnit currencyPaid = Monetary.getCurrency(object.getCurrencyPaid());
			if (object.getSinglePricePaid() != null)
				model.setSinglePricePaid(Money.of(object.getSinglePricePaid(), currencyPaid));
			if (object.getTotalPricePaid() != null)
				model.setTotalPricePaid(Money.of(object.getTotalPricePaid(), currencyPaid));
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

		// Ignore provided lots, lot delivery is a complex operation and
		// should be always done using the lot endpoint
		model.setLots(new IdentifiedEntity.UnprovidedSet<>());

		return model;
	}
}
