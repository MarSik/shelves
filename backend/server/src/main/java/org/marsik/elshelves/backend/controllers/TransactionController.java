package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.entities.TransactionApiModel;
import org.marsik.elshelves.backend.entities.Transaction;
import org.marsik.elshelves.backend.entities.converters.EmberToTransaction;
import org.marsik.elshelves.backend.entities.converters.TransactionToEmber;
import org.marsik.elshelves.backend.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transactions")
public class TransactionController extends AbstractRestController<Transaction, TransactionApiModel, TransactionService> {
	@Autowired
	public TransactionController(TransactionService service,
								 TransactionToEmber dbToRest,
								 EmberToTransaction restToDb) {
		super(TransactionApiModel.class, service, dbToRest, restToDb);
	}
}
