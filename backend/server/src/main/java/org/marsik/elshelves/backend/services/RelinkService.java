package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.IdentifiedEntityInterface;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.OwnedEntityInterface;
import org.marsik.elshelves.backend.entities.Sku;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.marsik.elshelves.backend.repositories.IdentifiedEntityRepository;
import org.marsik.elshelves.backend.repositories.OwnedEntityRepository;
import org.marsik.elshelves.backend.repositories.SkuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class RelinkService {
    private static final Logger log = LoggerFactory.getLogger(RelinkService.class);

    @Autowired
    UuidGenerator uuidGenerator;

    @Autowired
    OwnedEntityRepository ownedEntityRepository;

    @Autowired
    IdentifiedEntityRepository identifiedEntityRepository;

    @Autowired
    SkuRepository skuRepository;

    public static class RelinkContext implements Relinker {
        final Map<UUID, IdentifiedEntityInterface> cache = new THashMap<>();
        final IdentifiedEntityRepository identifiedEntityRepository;
        final SkuRepository skuRepository;
        final UuidGenerator uuidGenerator;
        User currentUser;

        public RelinkContext(IdentifiedEntityRepository identifiedEntityRepository,
                             SkuRepository skuRepository,
                             UuidGenerator uuidGenerator) {
            this.identifiedEntityRepository = identifiedEntityRepository;
            this.uuidGenerator = uuidGenerator;
            this.skuRepository = skuRepository;
        }

        public RelinkContext currentUser(User user) {
            this.currentUser = user;
            return this;
        }

        public RelinkContext addToCache(UUID id, IdentifiedEntityInterface entity) {
            if (cache.containsKey(id) && cache.get(id) != entity) {
                log.warn("Replacing cached {} with {}", entity.getId(), entity.toString());
            }

            cache.put(id, entity);
            return this;
        }

        public RelinkContext addToCache(IdentifiedEntityInterface entity) {
            return addToCache(entity.getId(), entity);
        }

        public IdentifiedEntityInterface get(UUID id) {
            return cache.get(id);
        }

        /**
         * This is a wrapper method that allows a context call to relink
         * a complex entity with relationships.
         *
         * @param value Entity with relationships
         * @return Relinked entity
         */
        public IdentifiedEntityInterface relink(IdentifiedEntityInterface value) {
            if (value != null) {
                value.relink(this);
            }

            return value;
        }

        /**
         * This call looks at the value's ID and tries to find an existing version
         * of it - it first consults the relink cache and then the database.
         *
         * @param value Template value used for getting the ID
         * @return Existing object or the template
         */
        public <T extends IdentifiedEntityInterface> T findExisting(T value)  {
            if (value == null) {
                return null;
            }

            // Generate UUID if there is none
            if (value.getId() == null) {
                value.setId(uuidGenerator.generate());
                log.error("Entity {} without UUID", value.getClass().getName());
                return value;
            }

            // Consult the relink cache first
            if (cache.containsKey(value.getId())) {
                return (T)cache.get(value.getId());
            }

            // Try getting the instance from database
            IdentifiedEntity entity = identifiedEntityRepository.findById(value.getId());
            if (entity != null
                    && entity instanceof OwnedEntity
                    && currentUser != null
                    && !((OwnedEntity) entity).getOwner().equals(currentUser)) {
                log.error("Entity {} belongs to user {} - changing the UUID", entity, ((OwnedEntity) entity).getOwner());

                entity.setId(uuidGenerator.generate());
                addToCache(entity);

                return (T)entity;
            }

            if (entity != null) {
                addToCache(entity);
                return (T)entity;
            }

            // Return the entity itself if it does not exist in the DB yet
            return value;
        }

        public Sku findExistingSku(Sku sku) {
            if (currentUser == null) {
                return sku;
            }

            Sku item =  skuRepository.findByTypeOwnerAndSourceIdAndSku(currentUser, sku.getSource().getId(), sku.getSku());
            return item == null ? sku : item;
        }

        /**
         * Make sure the object won't collide with an already existing entity, but
         * make sure the cache still knows the old id for relinking purposes.
         *
         * This method also sets the owner for the entity.
         *
         * @param value Entity about to be imported
         * @return The value entity prepared for import
         */
        @SuppressWarnings("unchecked")
        public <T extends IdentifiedEntityInterface> T fixUuid(T value)  {
            if (value.getId() == null) {
                value.setId(uuidGenerator.generate());
                log.warn("Entity {} without UUID -> {}", value.getClass().getName(), value.getId().toString());
            }

            // Remove a collision with an existing object, be sure everything is relinked after fixing
            IdentifiedEntity e = identifiedEntityRepository.findById(value.getId());
            if (e != null) {
                log.warn("Entity {} with colliding UUID {}", value.getClass().getName(), value.getId().toString());

                // Create new object so we are not changing the id on an object that
                // might be already using it as key in some collection
                T fixed = (T)value.shallowClone();

                // Save a reference with the old id
                cache.put(value.getId(), fixed);

                // Save a reference with the new id
                fixed.setId(uuidGenerator.generate());
                addToCache(fixed);
                value = fixed;
            }

            return value;
        }

        public RelinkContext fixOwner(OwnedEntityInterface item, User user) {
            if (item != null) {
                ((OwnedEntityInterface)item).setOwner(user);
            }

            return this;
        }

        public RelinkContext ensureOwner(OwnedEntityInterface item, User user) {
            if (item != null
                    && ((OwnedEntityInterface)item).getOwner() == null) {
                ((OwnedEntityInterface)item).setOwner(user);
            }

            return this;
        }
    }


    public RelinkContext newRelinker() {
        return new RelinkContext(identifiedEntityRepository, skuRepository, uuidGenerator);
    }
}
