package org.marsik.elshelves.backend.controllers;

import com.google.common.collect.Sets;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.entities.UpdateableEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.repositories.BaseIdentifiedEntityRepository;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.AbstractRestService;
import org.marsik.elshelves.ember.EmberModel;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BinaryOperator;

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

    protected Set<String> processInclude(String include) {
        if (include == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(Sets.newHashSet(include.split(",")));
        }
    }

    @RequestMapping
    @ResponseBody
    @Transactional(readOnly = true)
    public ResponseEntity<EmberModel> getAll(@CurrentUser User currentUser,
                                             @RequestParam(value = "ids[]", required = false) UUID[] ids,
                                             @RequestParam(value = "include", required = false) String include) throws BaseRestException {
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
            allDtos.add(getDbToRest().convert(null, null, entity, cache, processInclude(include)));
        }

        EmberModel.Builder<E> builder = new EmberModel.Builder<E>(dtoClazz, allDtos);

        for (E entity: allDtos) {
            sideLoad(entity, builder);
        }

        // Empty list
        if (allItems.isEmpty()) {
            return ResponseEntity
                    .ok()
                    .lastModified(new DateTime().getMillis())
                    .body(builder.build());
        }

        T lastModified = allItems.stream().reduce(new BinaryOperator<T>() {
            @Override
            public T apply(T t, T t2) {
                if (t.getLastModified().isAfter(t2.getLastModified())) {
                    return t;
                } else {
                    return t2;
                }
            }
        }).get();

        return ResponseEntity
                .ok()
                .lastModified(lastModified.getLastModified().getMillis())
                .body(builder.build());
    }

    @RequestMapping("/{id}")
    @ResponseBody
    @Transactional(readOnly = true)
    public ResponseEntity<EmberModel> getOne(@CurrentUser User currentUser,
                                             @PathVariable("id") UUID uuid,
                                             @RequestParam(value = "include", required = false) String include) throws BaseRestException {
        T entity = service.get(uuid, currentUser);
        E dto = getDbToRest().convert(null, null, entity, new THashMap<>(), processInclude(include));

        EmberModel.Builder<E> builder = new EmberModel.Builder<E>(dto);
        sideLoad(dto, builder);

        return ResponseEntity
                .ok()
                .eTag(dto.getVersion().toString())
                .lastModified(entity.getLastModified().getMillis())
                .body(builder.build());
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
