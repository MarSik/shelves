package org.marsik.elshelves.backend.services;

import gnu.trove.set.hash.THashSet;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.LotHistoryApiModel;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.api.entities.SourceApiModel;
import org.marsik.elshelves.api.entities.TransactionApiModel;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.Item;
import org.marsik.elshelves.backend.entities.LotHistory;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.Transaction;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
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
public class ItemService extends AbstractRestService<ItemRepository, Item> {
	@Autowired
	RequirementService requirementService;

    @Autowired
    DocumentService documentService;

    @Autowired
    IdentifiedEntityRepository identifiedEntityRepository;

	@Autowired
	public ItemService(ItemRepository repository,
                       UuidGenerator uuidGenerator) {
		super(repository, uuidGenerator);
	}

    @Override
    protected Iterable<Item> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }

	@Override
	protected void deleteEntity(Item entity) throws OperationNotPermitted {
		// Delete requirements
		for (Requirement r: entity.getRequires()) {
			requirementService.deleteEntity(r);
		}

		super.deleteEntity(entity);
	}

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

    @Transactional
    public Item startProject(Item item,
                             Type type,
                             Source source,
                             User currentUser) {
        Purchase purchase = item.getPurchase();
        Transaction transaction = purchase != null ? purchase.getTransaction() : null;
        List<IdentifiedEntity> created = new ArrayList<>();

        item.setId(uuidGenerator.generate());

        RelinkService.RelinkContext relinkContext = relinkService.newRelinker();
        relinkContext.addToCache(currentUser);

        if (type == null) {
            type = new Type();
            type.setId(uuidGenerator.generate());
            type.setName(item.getSerials().isEmpty() ? item.getName() : item.getSerials().iterator().next());
            created.add(type);
        } else {
            type = relinkContext.findExisting(type);
        }

        if (purchase == null) {
            purchase = new Purchase();
            purchase.setCount(1L);
            purchase.setId(uuidGenerator.generate());
            purchase.addLot(item);
            purchase.setType(type);

            created.add(purchase);
            item.setPurchase(purchase);
        }

        type.addPurchase(purchase);

        if (source == null) {
            source = new Source();
            source.setId(uuidGenerator.generate());
            source.setName("Project source");
            created.add(source);
        }

        if (transaction == null) {
            transaction = new Transaction();
            transaction.setId(uuidGenerator.generate());
            transaction.setName(item.getSerials().isEmpty() ? "Project start" : item.getSerials().iterator().next());
            transaction.setDate(new DateTime());
            transaction.addItem(purchase);
            transaction.setSource(source);

            created.add(transaction);
            purchase.setTransaction(transaction);
        }

        LotHistory history = new LotHistory();
        history.setId(uuidGenerator.generate());
        history.setCreated(new DateTime());
        history.setAction(LotAction.DELIVERY);
        history.setPerformedBy(currentUser);
        created.add(history);

        item.setCount(1L);
        item.setRequires(new THashSet<>());
        item.setStatus(LotAction.DELIVERY);
        item.setHistory(history);
        item.setPurchase(purchase);
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
        identifiedEntityRepository.save(source);
        identifiedEntityRepository.save(transaction);
        identifiedEntityRepository.save(purchase);
        identifiedEntityRepository.save(created);
        identifiedEntityRepository.save(item);
        identifiedEntityRepository.save(history);

        return item;
    }

    @Override
    protected Item save(Item entity) {
        saveOrUpdate(entity.getHistory());
        saveOrUpdate(entity.getPurchase());
        for (Requirement r: entity.getRequires()) {
            saveOrUpdate(r);
        }
        return super.save(entity);
    }
}
