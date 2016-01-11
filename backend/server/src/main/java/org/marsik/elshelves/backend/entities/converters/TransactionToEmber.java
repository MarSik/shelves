package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.api.entities.TransactionApiModel;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.LotHistory;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class TransactionToEmber extends AbstractEntityToEmber<Transaction, TransactionApiModel> {
	@Autowired
	PurchaseToEmber purchaseToEmber;

	@Autowired
	SourceToEmber sourceToEmber;

	@Autowired
	UserToEmber userToEmber;

    @Autowired
    NamedObjectToEmber namedObjectToEmber;

	@Autowired
	EntityToEmberConversionService conversionService;

	public TransactionToEmber() {
		super(TransactionApiModel.class);
	}

	@PostConstruct
	void postConstruct() {
		conversionService.register(Transaction.class, getTarget(), this);

	}

	@Override
	public TransactionApiModel convert(String path, Transaction object, TransactionApiModel model, Map<UUID, Object> cache, Set<String> include) {
		namedObjectToEmber.convert(path, object, model, cache, include);

		model.setDate(object.getDate());
		model.setExpectedDelivery(object.getExpectedDelivery());

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

		model.setSource(sourceToEmber.convert(path, "source", object.getSource(), cache, include));

		if (object.getItems() != null) {
			model.setItems(new THashSet<PurchaseApiModel>());
			for (Purchase p : object.getItems()) {
				model.getItems().add(purchaseToEmber.convert(path, "item", p, cache, include));
			}
		}

		return model;
	}
}
