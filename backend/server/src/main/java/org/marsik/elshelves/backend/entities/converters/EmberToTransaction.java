package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.api.entities.TransactionApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
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
public class EmberToTransaction extends AbstractEmberToEntity<TransactionApiModel, Transaction> {
	@Autowired
	EmberToUser emberToUser;

	@Autowired
	EmberToPurchase emberToPurchase;

	@Autowired
	EmberToSource emberToSource;

    @Autowired
    EmberToNamedObject emberToNamedObject;

	@Autowired
	EmberToEntityConversionService conversionService;

	public EmberToTransaction() {
		super(Transaction.class);
	}

	@PostConstruct
	void postConstruct() {
		conversionService.register(TransactionApiModel.class, getTarget(), this);

	}

	@Override
	public Transaction convert(String path, TransactionApiModel object, Transaction model, Map<UUID, Object> cache, Set<String> include) {
		emberToNamedObject.convert(path, object, model, cache, include);

		model.setSource(emberToSource.convert(path, "source", object.getSource(), cache, include));
		model.setDate(object.getDate());
		model.setExpectedDelivery(object.getExpectedDelivery());

		if (object.getItems() != null) {
			model.setItems(new THashSet<Purchase>());
			for (PurchaseApiModel p : object.getItems()) {
				model.addItem(emberToPurchase.convert(path, "purchase", p, cache, include));
			}
		} else {
			model.setItems(new IdentifiedEntity.UnprovidedSet<>());
		}

		return model;
	}
}
