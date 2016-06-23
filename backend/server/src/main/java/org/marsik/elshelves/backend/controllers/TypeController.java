package org.marsik.elshelves.backend.controllers;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToType;
import org.marsik.elshelves.backend.entities.converters.EntityToEmberConversionService;
import org.marsik.elshelves.backend.entities.converters.TypeToEmber;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.PurchaseService;
import org.marsik.elshelves.backend.services.TypeService;
import org.marsik.elshelves.ember.EmberModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/types")
public class TypeController extends AbstractRestController<Type, PartTypeApiModel, TypeService> {

	@Autowired
	PurchaseService purchaseService;

	@Autowired
	EntityToEmberConversionService entityToEmberConversionService;

	@Autowired
	public TypeController(TypeService service,
						  TypeToEmber dbToRest,
						  EmberToType restToDb) {
		super(PartTypeApiModel.class, service, dbToRest, restToDb);
	}

	@RequestMapping("/{id}/undelivered")
	@ResponseBody
	@Transactional(readOnly = true)
	public ResponseEntity<EmberModel> getUndelivered(@CurrentUser User currentUser,
									 @PathVariable("id") UUID uuid,
									 @RequestParam(value = "include", required = false) String include) throws BaseRestException {
		Type type = service.get(uuid, currentUser);
		Collection<Purchase> allItems = purchaseService.findUndelivered(currentUser, type);

		Collection<PurchaseApiModel> allDtos = new THashSet<>();
		Map<UUID, Object> cache = new THashMap<>();
		final Set<String> includes = processInclude(include);

		for (Purchase entity : allItems) {
			allDtos.add(entityToEmberConversionService.converter(entity, PurchaseApiModel.class)
					.convert(null, null, entity, cache, includes));
		}

		EmberModel.Builder<PurchaseApiModel> builder = new EmberModel.Builder<PurchaseApiModel>(allDtos);

		for (PurchaseApiModel entity : allDtos) {
			cache.remove(entity.getId());
		}

		return ResponseEntity
				.ok()
				.body(builder.build());
	}
}
