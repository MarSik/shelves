package org.marsik.elshelves.backend.controllers;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.apache.commons.io.FileUtils;
import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.controllers.exceptions.InvalidRequest;
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
import org.springframework.http.ResponseEntity;
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

public class AbstractRestController<T extends UpdateableEntity, E extends AbstractEntityApiModel, S extends AbstractRestService<? extends BaseIdentifiedEntityRepository<T>, T>> extends AbstractReadOnlyRestController<T,E,S> {

    final CachingConverter<E, T, UUID> restToDb;

    public AbstractRestController(Class<E> dtoClazz,
                                  S service,
                                  CachingConverter<T, E, UUID> dbToRest,
                                  CachingConverter<E, T, UUID> restToDb) {
        super(dtoClazz, dbToRest, service);
        this.restToDb = restToDb;
    }


    public CachingConverter<E, T, UUID> getRestToDb() {
        return restToDb;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public ResponseEntity<EmberModel> create(@CurrentUser User currentUser,
                             @Valid @RequestBody E item) throws OperationNotPermitted {
        T incoming = getRestToDb().convert(item, Integer.MAX_VALUE, new THashMap<>());
        incoming = service.create(incoming, currentUser);

        // Flush is needed to get the updated version
        service.flush();

        E entity = getDbToRest().convert(incoming, 1, new THashMap<>());

        EmberModel.Builder<E> builder = new EmberModel.Builder<E>(entity);
        sideLoad(entity, builder);
        return ResponseEntity
                .ok()
                .eTag(entity.getVersion().toString())
                .lastModified(incoming.getLastModified().getMillis())
                .body(builder.build());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    @Transactional
    public ResponseEntity<EmberModel> update(@CurrentUser User currentUser,
                             @PathVariable("id") UUID uuid,
                             @Valid @RequestBody E item) throws BaseRestException {
        T update = getRestToDb().convert(item, Integer.MAX_VALUE, new THashMap<>());

        // The REST entity does not contain id during PUT, because that is
        // provided by the URL
        update.setId(uuid);
        T entity = service.update(update, currentUser);

        // Flush is needed to get the updated version
        service.flush();

        E result = getDbToRest().convert(entity, 1, new THashMap<>());
        EmberModel.Builder<E> builder = new EmberModel.Builder<E>(result);
        sideLoad(result, builder);

        return ResponseEntity
                .ok()
                .eTag(entity.getVersion().toString())
                .lastModified(entity.getLastModified().getMillis())
                .body(builder.build());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @Transactional
    public Map<Object, Object> deleteOne(@CurrentUser User currentUser,
                                         @PathVariable("id") UUID uuid) throws BaseRestException {
        service.delete(uuid, currentUser);
        return new THashMap<>();
    }

}
