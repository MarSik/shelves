package org.marsik.elshelves.backend.controllers;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.AbstractRestService;
import org.springframework.data.neo4j.repository.GraphRepository;
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

public class AbstractRestController<T extends OwnedEntity, E extends AbstractEntityApiModel, S extends AbstractRestService<? extends GraphRepository<T>, T, E>> {

    final Class<E> dtoClazz;
    final S service;

    public AbstractRestController(Class<E> dtoClazz, S service) {
        this.dtoClazz = dtoClazz;
        this.service = service;
    }

    protected void sideLoad(E dto, EmberModel.Builder<E> builder) {

    }

    @RequestMapping
    @ResponseBody
    @Transactional(readOnly = true)
    public EmberModel getAll(@CurrentUser User currentUser,
							 @RequestParam(value = "ids[]", required = false) UUID[] ids) throws EntityNotFound, PermissionDenied {
		Collection<E> allItems;

		if (ids == null) {
			allItems = service.getAllItems(currentUser);
		} else {
			allItems = new THashSet<>();
			for (UUID id: ids) {
				allItems.add(service.get(id, currentUser));
			}
		}

        EmberModel.Builder<E> builder = new EmberModel.Builder<E>(dtoClazz, allItems);

        for (E entity: allItems) {
            sideLoad(entity, builder);
        }

        return builder.build();
    }

    @RequestMapping("/{id}")
    @ResponseBody
    @Transactional(readOnly = true)
    public EmberModel getOne(@CurrentUser User currentUser,
                             @PathVariable("id") UUID uuid) throws PermissionDenied, EntityNotFound {
        E entity = service.get(uuid, currentUser);
        EmberModel.Builder<E> builder = new EmberModel.Builder<E>(entity);
        sideLoad(entity, builder);
        return builder.build();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public EmberModel create(@CurrentUser User currentUser,
                             @Valid @RequestBody E item) throws OperationNotPermitted {
        E entity = service.create(item, currentUser);
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
        E entity = service.update(uuid, item, currentUser);
        EmberModel.Builder<E> builder = new EmberModel.Builder<E>(entity);
        sideLoad(entity, builder);
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
