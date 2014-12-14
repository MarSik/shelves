package org.marsik.elshelves.backend.controllers;

import gnu.trove.map.hash.THashMap;
import org.apache.commons.beanutils.BeanUtils;
import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.AbstractEntity;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.UuidGenerator;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AbstractRestController<T extends OwnedEntity, E extends AbstractEntity> {

    final GraphRepository<T> repository;
    final CachingConverter<T, E, UUID> dbToRest;
    final CachingConverter<E, T, UUID> restToDb;
    final UuidGenerator uuidGenerator;

    public AbstractRestController(GraphRepository<T> repository, CachingConverter<T, E, UUID> dbToRest, CachingConverter<E, T, UUID> restToDb, UuidGenerator uuidGenerator) {
        this.repository = repository;
        this.dbToRest = dbToRest;
        this.restToDb = restToDb;
        this.uuidGenerator = uuidGenerator;
    }

    @RequestMapping
    @ResponseBody
    @Transactional
    public EmberModel getAll(@CurrentUser User currentUser) {
        List<E> list = new ArrayList<>();

        Map<UUID, Object> cache = new THashMap<>();

        for (T item: repository.findAll()) {
            list.add(dbToRest.convert(item, cache));
        }

        /* XXX fix the collection name in EmberModel */

        return new EmberModel.Builder<E>(list).build();
    }

    @RequestMapping("/{id}")
    @ResponseBody
    @Transactional
    public EmberModel getOne(@CurrentUser User currentUser,
                             @PathVariable("id") UUID uuid) {
        T one = repository.findBySchemaPropertyValue("uuid", uuid);

        return new EmberModel.Builder<E>(dbToRest.convert(one, new THashMap<UUID, Object>())).build();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public EmberModel create(@CurrentUser User currentUser,
                             @RequestBody E item) {
        T create = restToDb.convert(item, new THashMap<UUID, Object>());
        create.setUuid(uuidGenerator.generate());
        create.setOwner(currentUser);

        repository.save(create);

        return new EmberModel.Builder<E>(dbToRest.convert(create, new THashMap<UUID, Object>())).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    @Transactional
    public EmberModel create(@CurrentUser User currentUser,
                             @PathVariable("id") UUID uuid,
                             @RequestBody E item) throws IllegalAccessException, InvocationTargetException {
        T one = repository.findBySchemaPropertyValue("uuid", uuid);
        T update = restToDb.convert(item, new THashMap<UUID, Object>());

        BeanUtils.copyProperties(one, update);
        repository.save(one);

        return new EmberModel.Builder<E>(dbToRest.convert(one, new THashMap<UUID, Object>())).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @Transactional
    public Map<Object, Object> deleteOne(@CurrentUser User currentUser,
                                @PathVariable("id") UUID uuid) {
        T one = repository.findBySchemaPropertyValue("uuid", uuid);
        repository.delete(one);

        return new THashMap<>();
    }
}
