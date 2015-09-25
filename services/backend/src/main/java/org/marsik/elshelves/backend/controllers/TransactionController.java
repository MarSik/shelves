package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.entities.TransactionApiModel;
import org.marsik.elshelves.backend.entities.Transaction;
import org.marsik.elshelves.backend.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transactions")
public class TransactionController extends AbstractRestController<Transaction, TransactionApiModel, TransactionService> {
	@Autowired
	public TransactionController(TransactionService service) {
		super(TransactionApiModel.class, service);
	}
}
