package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.api.entities.TransactionApiModel;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToTransaction extends AbstractEmberToEntity<TransactionApiModel, Transaction> {
	@Autowired
	EmberToUser emberToUser;

	@Autowired
	EmberToPurchase emberToPurchase;

	@Autowired
	EmberToSource emberToSource;

    @Autowired
    EmberToNamedObject emberToNamedObject;

	public EmberToTransaction() {
		super(Transaction.class);
	}

	@Override
	public Transaction convert(TransactionApiModel object, Transaction model, int nested, Map<UUID, Object> cache) {
		emberToNamedObject.convert(object, model, nested, cache);

		if (nested == 0) {
			return model;
		}

		model.setSource(emberToSource.convert(object.getSource(), nested - 1, cache));

		if (object.getItems() != null) {
			model.setItems(new THashSet<Purchase>());
			for (PurchaseApiModel p : object.getItems()) {
				model.addItem(emberToPurchase.convert(p, nested - 1, cache));
			}
		}

		return model;
	}
}
