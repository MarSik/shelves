package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.api.entities.TransactionApiModel;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.LotHistory;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class TransactionToEmber extends AbstractEntityToEmber<Transaction, TransactionApiModel> {
	@Autowired
	PurchaseToEmber purchaseToEmber;

	@Autowired
	SourceToEmber sourceToEmber;

	@Autowired
	UserToEmber userToEmber;

    @Autowired
    NamedObjectToEmber namedObjectToEmber;

	public TransactionToEmber() {
		super(TransactionApiModel.class);
	}

	@Override
	public TransactionApiModel convert(Transaction object, TransactionApiModel model, int nested, Map<UUID, Object> cache) {
		namedObjectToEmber.convert(object, model, nested, cache);

		if (nested == 0) {
			return model;
		}

		model.setDate(object.getDate());

		/* Backup way of getting the transaction date to workaround a bug in older version
		   of EmberToLot converter */
		if (model.getDate() == null) {
			for (Purchase p: object.getItems()) {
				for (Lot l: p.getLots()) {
					if (model.getDate() == null) {
						model.setDate(l.getCreated());
					} else if (l.getCreated().compareTo(model.getDate()) < 0) {
						model.setDate(l.getCreated());
					}

					LotHistory h = l.getHistory();
					while (h != null) {
						if (h.getCreated().compareTo(model.getDate()) < 0) {
							model.setDate(h.getCreated());
						}
						h = h.getPrevious();
					}
				}
			}

			if (model.getDate() == null) {
				model.setDate(object.getCreated());
			}
		}

		model.setSource(sourceToEmber.convert(object.getSource(), nested - 1, cache));

		if (object.getItems() != null) {
			model.setItems(new THashSet<PurchaseApiModel>());
			for (Purchase p : object.getItems()) {
				model.getItems().add(purchaseToEmber.convert(p, nested - 1, cache));
			}
		}

		return model;
	}
}
