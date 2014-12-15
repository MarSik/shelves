package org.marsik.elshelves.backend.controllers;

import gnu.trove.map.hash.THashMap;
import org.apache.commons.beanutils.BeanUtils;
import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.AbstractEntity;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.AbstractRestService;
import org.marsik.elshelves.backend.services.UuidGenerator;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AbstractRestController<T extends OwnedEntity, E extends AbstractEntity> {

    final Class<E> dtoClazz;
    final AbstractRestService<T, E> service;

    public AbstractRestController(Class<E> dtoClazz, AbstractRestService<T, E> service) {
        this.dtoClazz = dtoClazz;
        this.service = service;
    }

    @RequestMapping
    @ResponseBody
    @Transactional
    public EmberModel getAll(@CurrentUser User currentUser) {
        return new EmberModel.Builder<E>(dtoClazz, service.getAllItems(currentUser)).build();
    }

    @RequestMapping("/{id}")
    @ResponseBody
    @Transactional
    public EmberModel getOne(@CurrentUser User currentUser,
                             @PathVariable("id") UUID uuid) throws PermissionDenied {
        return new EmberModel.Builder<E>(service.get(uuid, currentUser)).build();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public EmberModel create(@CurrentUser User currentUser,
                             @Valid @RequestBody E item) {
        return new EmberModel.Builder<E>(service.create(item, currentUser)).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    @Transactional
    public EmberModel update(@CurrentUser User currentUser,
                             @PathVariable("id") UUID uuid,
                             @Valid @RequestBody E item) throws IllegalAccessException, InvocationTargetException, PermissionDenied {
        return new EmberModel.Builder<E>(service.update(uuid, item, currentUser)).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @Transactional
    public Map<Object, Object> deleteOne(@CurrentUser User currentUser,
                                         @PathVariable("id") UUID uuid) throws PermissionDenied {
        service.delete(uuid, currentUser);
        return new THashMap<>();
    }
}
