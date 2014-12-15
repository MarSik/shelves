package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.apache.commons.beanutils.BeanUtils;
import org.marsik.elshelves.api.entities.AbstractEntity;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class AbstractRestService<T extends OwnedEntity, E extends AbstractEntity> {
    final GraphRepository<T> repository;
    final CachingConverter<T, E, UUID> dbToRest;
    final CachingConverter<E, T, UUID> restToDb;
    final UuidGenerator uuidGenerator;

    public AbstractRestService(GraphRepository<T> repository,
                               CachingConverter<T, E, UUID> dbToRest,
                               CachingConverter<E, T, UUID> restToDb,
                               UuidGenerator uuidGenerator) {
        this.repository = repository;
        this.dbToRest = dbToRest;
        this.restToDb = restToDb;
        this.uuidGenerator = uuidGenerator;
    }

    protected Iterable<T> getAllEntities(User currentUser) {
        return repository.findAll();
    }

    protected T getSingleEntity(UUID uuid) {
        return repository.findBySchemaPropertyValue("uuid", uuid);
    }

    protected T createEntity(E dto, User currentUser) {
        T created = restToDb.convert(dto, new THashMap<UUID, Object>());
        created.setUuid(uuidGenerator.generate());
        created.setOwner(currentUser);
        return created;
    }

    protected T updateEntity(T entity, E dto) throws IllegalAccessException, InvocationTargetException {
        BeanUtils.copyProperties(entity, dto);
        return entity;
    }

    public Collection<E> getAllItems(User currentUser) {
        List<E> dtos = new ArrayList<>();
        for (T entity: getAllEntities(currentUser)) {
            dtos.add(dbToRest.convert(entity, new THashMap<UUID, Object>()));
        }
        return dtos;
    }

    public E create(E dto, User currentUser) {
        T created = createEntity(dto, currentUser);
        repository.save(created);
        return dbToRest.convert(created, new THashMap<UUID, Object>());
    }

    public E get(UUID uuid, User currentUser) throws PermissionDenied {
        T one = getSingleEntity(uuid);

        if (!one.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        return dbToRest.convert(one, new THashMap<UUID, Object>());
    }

    public boolean delete(UUID uuid, User currentUser) throws PermissionDenied {
        T one = getSingleEntity(uuid);

        if (!one.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        repository.delete(one);
        return true;
    }

    public E update(UUID uuid, E dto, User currentUser) throws PermissionDenied {
        T one = getSingleEntity(uuid);

        if (!one.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        try {
            updateEntity(one, dto);
        } catch (InvocationTargetException|IllegalAccessException ex) {
            ex.printStackTrace();
        }

        return dbToRest.convert(one, new THashMap<UUID, Object>());
    }
}
