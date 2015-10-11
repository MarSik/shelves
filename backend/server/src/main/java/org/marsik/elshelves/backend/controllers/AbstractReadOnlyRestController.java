package org.marsik.elshelves.backend.controllers;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.apache.commons.io.FileUtils;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.UpdateableEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.repositories.BaseIdentifiedEntityRepository;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.AbstractRestService;
import org.marsik.elshelves.ember.EmberModel;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class AbstractReadOnlyRestController<T extends UpdateableEntity, E extends AbstractEntityApiModel, S extends AbstractRestService<? extends BaseIdentifiedEntityRepository<T>, T>> {
    final Class<E> dtoClazz;
    final S service;
    final CachingConverter<T, E, UUID> dbToRest;

    public AbstractReadOnlyRestController(Class<E> dtoClazz, CachingConverter<T, E, UUID> dbToRest, S service) {
        this.dtoClazz = dtoClazz;
        this.dbToRest = dbToRest;
        this.service = service;
    }

    public CachingConverter<T, E, UUID> getDbToRest() {
        return dbToRest;
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
