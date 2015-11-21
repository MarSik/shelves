package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.PolymorphicRecord;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class PurchaseToEmber extends AbstractEntityToEmber<Purchase, PurchaseApiModel> {
	@Autowired
	NamedObjectToEmber namedObjectToEmber;

	@Autowired
	LotToEmber lotToEmber;

	@Autowired
	TypeToEmber typeToEmber;

	@Autowired
	TransactionToEmber transactionToEmber;

	public PurchaseToEmber() {
		super(PurchaseApiModel.class);
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
			model.setLots(new THashSet<PolymorphicRecord>());
			for (Lot l: object.getLots()) {
				PolymorphicRecord r = new PolymorphicRecord();
				r.setId(l.getId());
				r.setType(l.getEmberType());
				model.getLots().add(r);
			}
		}

		return model;
	}
}
