package org.marsik.elshelves.backend.services;

import gnu.trove.set.hash.THashSet;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.Item;
import org.marsik.elshelves.backend.entities.LotHistory;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.marsik.elshelves.backend.repositories.IdentifiedEntityRepository;
import org.marsik.elshelves.backend.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ItemServiceImpl extends AbstractRestService<ItemRepository, Item> implements ItemService {
	@Autowired
    RequirementService requirementService;

    @Autowired
    DocumentService documentService;

    @Autowired
    IdentifiedEntityRepository identifiedEntityRepository;

	@Autowired
	public ItemServiceImpl(ItemRepository repository,
            UuidGenerator uuidGenerator) {
		super(repository, uuidGenerator);
	}

    @Override
    protected Iterable<Item> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }

	@Override
	public void deleteEntity(Item entity) throws OperationNotPermitted {
		// Delete requirements
		for (Requirement r: entity.getRequires()) {
			requirementService.deleteEntity(r);
		}

		super.deleteEntity(entity);
	}

    @Override
    public Item importRequirements(UUID projectId, UUID document, User currentUser, List<Requirement> newRequirements) throws OperationNotPermitted, EntityNotFound, PermissionDenied, IOException {
        Item item = getSingleEntity(projectId);

        if (item == null) {
            throw new EntityNotFound();
        }

        if (!item.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        Item dummyProject = new Item();
        dummyProject.setId(projectId);

        List<Requirement> requirements = documentService.analyzeSchematics(document, currentUser);
        for (Requirement r: requirements) {
            r.setItem(dummyProject);
            Requirement newR = requirementService.create(r, currentUser);
            newRequirements.add(newR);
        }

        return item;
    }

    @Override @Transactional
    public Item startProject(Item item,
                             Type type,
                             User currentUser) {
        List<IdentifiedEntity> created = new ArrayList<>();

        item.setType(type);
        item.setId(uuidGenerator.generate());

        Relinker relinkContext = relinkService.newRelinker();
        relinkContext
                .currentUser(currentUser)
                .addToCache(currentUser);

        if (type == null) {
            type = new Type();
            type.setId(uuidGenerator.generate());
            type.setName(item.getSerials().isEmpty() ? item.getName() : item.getSerials().iterator().next());
            created.add(type);
        } else {
            type = relinkContext.findExisting(type);
        }

        type.addLot(item);

        LotHistory history = new LotHistory();
        history.setId(uuidGenerator.generate());
        history.setValidSince(new DateTime());
        history.setAction(LotAction.DELIVERY);
        history.setPerformedBy(currentUser);
        created.add(history);

        item.setCount(1L);
        item.setRequires(new THashSet<>());
        item.setUsed(false);
        item.setUsedInPast(false);
        item.setValid(true);
        item.setHistory(history);
        item.setType(type);
        created.add(item);

        for (IdentifiedEntity en: created) {
            if (en instanceof OwnedEntity) {
                relinkContext.ensureOwner((OwnedEntity)en, currentUser);
            }

            relinkContext
                    .addToCache(en);
        }

        for (IdentifiedEntity en: created) {
            en.relink(relinkContext);
        }

        identifiedEntityRepository.save(type);
        identifiedEntityRepository.save(created);
        identifiedEntityRepository.save(item);
        identifiedEntityRepository.save(history);

        return item;
    }

    @Override
    protected Item save(Item entity) {
        saveOrUpdate(entity.getHistory());
        saveOrUpdate(entity.getType());
        for (Requirement r: entity.getRequires()) {
            saveOrUpdate(r);
        }
        return super.save(entity);
    }
}
