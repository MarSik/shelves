package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.dtos.LotSplitResult;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.LotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/lots")
public class LotController {
	@Autowired
	LotService lotService;

	protected void sideload(EmberModel.Builder<LotApiModel> modelBuilder,
							LotApiModel l) {
		modelBuilder.sideLoad(l.getBelongsTo());
		modelBuilder.sideLoad(l.getPerformedBy());
		modelBuilder.sideLoad(l.getType());
		modelBuilder.sideLoad(l.getLocation());
		modelBuilder.sideLoad(l.getPrevious());
	}

	@ResponseBody
	@RequestMapping
	@Transactional
	public EmberModel getAll(@CurrentUser User currentUser) {
		Collection<LotApiModel> lots = lotService.getAll(currentUser);

		EmberModel.Builder<LotApiModel> modelBuilder = new EmberModel.Builder<LotApiModel>(lots);
		for (LotApiModel l: lots) {
			sideload(modelBuilder, l);
		}

		return modelBuilder.build();
	}

	@RequestMapping("/{uuid}")
	@ResponseBody
	@Transactional
	public EmberModel getOne(@CurrentUser User currentUser,
							 @PathVariable("uuid") UUID id) throws PermissionDenied, EntityNotFound {
		LotApiModel lot = lotService.get(id, currentUser);
		EmberModel.Builder<LotApiModel> modelBuilder = new EmberModel.Builder<LotApiModel>(lot);
		sideload(modelBuilder, lot);
		return modelBuilder.build();
	}

	@RequestMapping("/{uuid}/next")
	@ResponseBody
	@Transactional
	public EmberModel getNext(@CurrentUser User currentUser,
							 @PathVariable("uuid") UUID id) throws PermissionDenied, EntityNotFound {
		Iterable<LotApiModel> lots = lotService.getNext(id, currentUser);
		EmberModel.Builder<LotApiModel> modelBuilder = new EmberModel.Builder<LotApiModel>(lots);
		for (LotApiModel l: lots) {
			sideload(modelBuilder, l);
		}
		return modelBuilder.build();
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public EmberModel split(@CurrentUser User currentUser,
							@RequestBody @Validated LotApiModel lot) throws PermissionDenied, EntityNotFound, OperationNotPermitted {
		LotSplitResult result = lotService.split(lot.getPrevious().getId(), lot.getCount(), currentUser);
		EmberModel.Builder<LotApiModel> modelBuilder = new EmberModel.Builder<LotApiModel>(result.getRequested());
		sideload(modelBuilder, result.getRemainder());
		return modelBuilder.build();
	}
}
