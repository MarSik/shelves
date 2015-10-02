package org.marsik.elshelves.backend.controllers;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.apache.commons.io.FileUtils;
import org.marsik.elshelves.ember.EmberModel;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.UpdateableEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.repositories.BaseIdentifiedEntityRepository;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.AbstractRestService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class AbstractRestController<T extends UpdateableEntity, E extends AbstractEntityApiModel, S extends AbstractRestService<? extends BaseIdentifiedEntityRepository<T>, T>> {

    final Class<E> dtoClazz;
    final S service;
    final CachingConverter<T, E, UUID> dbToRest;
    final CachingConverter<E, T, UUID> restToDb;

    public AbstractRestController(Class<E> dtoClazz,
                                  S service,
                                  CachingConverter<T, E, UUID> dbToRest,
                                  CachingConverter<E, T, UUID> restToDb) {
        this.dtoClazz = dtoClazz;
        this.service = service;
        this.dbToRest = dbToRest;
        this.restToDb = restToDb;
    }


    public CachingConverter<T, E, UUID> getDbToRest() {
        return dbToRest;
    }

    public CachingConverter<E, T, UUID> getRestToDb() {
        return restToDb;
    }

    protected void sideLoad(E dto, EmberModel.Builder<E> builder) {

    }

    @RequestMapping
    @ResponseBody
    @Transactional(readOnly = true)
    public EmberModel getAll(@CurrentUser User currentUser,
							 @RequestParam(value = "ids[]", required = false) UUID[] ids) throws EntityNotFound, PermissionDenied {
		Collection<T> allItems;

		if (ids == null) {
			allItems = service.getAllItems(currentUser);
		} else {
			allItems = new THashSet<>();
			for (UUID id: ids) {
				allItems.add(service.get(id, currentUser));
			}
		}

        Collection<E> allDtos = new THashSet<>();
        Map<UUID, Object> cache = new THashMap<>();

        for (T entity: allItems) {
            allDtos.add(getDbToRest().convert(entity, 1, cache));
        }

        EmberModel.Builder<E> builder = new EmberModel.Builder<E>(dtoClazz, allDtos);

        for (E entity: allDtos) {
            sideLoad(entity, builder);
        }

        return builder.build();
    }

    @RequestMapping("/{id}")
    @ResponseBody
    @Transactional(readOnly = true)
    public EmberModel getOne(@CurrentUser User currentUser,
                             @PathVariable("id") UUID uuid) throws PermissionDenied, EntityNotFound {
        T entity = service.get(uuid, currentUser);
        E dto = getDbToRest().convert(entity, 1, new THashMap<>());

        EmberModel.Builder<E> builder = new EmberModel.Builder<E>(dto);
        sideLoad(dto, builder);
        return builder.build();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public EmberModel create(@CurrentUser User currentUser,
                             @Valid @RequestBody E item) throws OperationNotPermitted {
        T incoming = getRestToDb().convert(item, Integer.MAX_VALUE, new THashMap<>());
        incoming = service.create(incoming, currentUser);

        E entity = getDbToRest().convert(incoming, 1, new THashMap<>());

        EmberModel.Builder<E> builder = new EmberModel.Builder<E>(entity);
        sideLoad(entity, builder);
        return builder.build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    @Transactional
    public EmberModel update(@CurrentUser User currentUser,
                             @PathVariable("id") UUID uuid,
                             @Valid @RequestBody E item) throws IllegalAccessException, InvocationTargetException, OperationNotPermitted, PermissionDenied, EntityNotFound {
        T update = getRestToDb().convert(item, Integer.MAX_VALUE, new THashMap<>());

        // The REST entity does not contain id during PUT, because that is
        // provided by the URL
        update.setId(uuid);
        T entity = service.update(update, currentUser);

        E result = getDbToRest().convert(entity, 1, new THashMap<>());
        EmberModel.Builder<E> builder = new EmberModel.Builder<E>(result);
        sideLoad(result, builder);
        return builder.build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @Transactional
    public Map<Object, Object> deleteOne(@CurrentUser User currentUser,
                                         @PathVariable("id") UUID uuid) throws PermissionDenied, OperationNotPermitted, EntityNotFound {
        service.delete(uuid, currentUser);
        return new THashMap<>();
    }

	protected S getService() {
		return service;
	}

	protected HttpServletResponse sendFile(HttpServletResponse response, String contentType, File f) throws IOException {
		response.setContentType(contentType);
		response.setHeader("Content-Length", Long.toString(f.length()));
		FileUtils.copyFile(f, response.getOutputStream());
		return response;
	}
}
