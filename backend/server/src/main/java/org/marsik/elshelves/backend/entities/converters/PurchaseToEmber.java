package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class PurchaseToEmber extends AbstractEntityToEmber<Purchase, PurchaseApiModel> {
	@Autowired
	NamedObjectToEmber namedObjectToEmber;

	@Autowired
	LotToEmber lotToEmber;

	@Autowired
	TypeToEmber typeToEmber;

	@Autowired
	TransactionToEmber transactionToEmber;

	@Autowired
	EntityToEmberConversionService conversionService;

	public PurchaseToEmber() {
		super(PurchaseApiModel.class);
	}

	@PostConstruct
	void postConstruct() {
		conversionService.register(Purchase.class, this);

	}

	@Override
	public PurchaseApiModel convert(String path, Purchase object, PurchaseApiModel model, Map<UUID, Object> cache, Set<String> include) {
		model.setCount(object.getCount());

		if (object.getSinglePrice() != null)
			model.setSinglePrice(object.getSinglePrice().getAmount());
		if (object.getTotalPrice() != null)
			model.setTotalPrice(object.getTotalPrice().getAmount());
		if (object.getSinglePrice() != null)
			model.setCurrency(object.getSinglePrice().getCurrencyUnit().getCurrencyCode());

		if (object.getSinglePricePaid() != null)
			model.setSinglePricePaid(object.getSinglePricePaid().getAmount());
		if (object.getTotalPricePaid() != null)
			model.setTotalPricePaid(object.getTotalPricePaid().getAmount());
		if (object.getSinglePricePaid() != null)
			model.setCurrencyPaid(object.getSinglePricePaid().getCurrencyUnit().getCurrencyCode());

		model.setVat(object.getVat());
		model.setVatIncluded(object.getVatIncluded());
        model.setMissing(object.getMissing());

		model.setType(typeToEmber.convert(path, "type", object.getType(), cache, include));
		model.setTransaction(transactionToEmber.convert(path, "transaction", object.getTransaction(), cache, include));

		if (object.getSku() != null) {
			model.setSku(object.getSku().getSku());
		}

		if (object.getLots() != null) {
			model.setLots(new THashSet<>());
			for (Lot l: object.getLots()) {
				LotApiModel r = conversionService.converter(l, LotApiModel.class)
						.convert(path, "lot", l, cache, include);
				model.getLots().add(r);
			}
		}

		return model;
	}
}
