package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.api.entities.BoxApiModel;
import org.marsik.elshelves.api.entities.ItemApiModel;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.LotHistoryApiModel;
import org.marsik.elshelves.api.entities.PartGroupApiModel;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.api.entities.PolymorphicRecord;
import org.marsik.elshelves.api.entities.ProjectApiModel;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.api.entities.RestoreApiModel;
import org.marsik.elshelves.api.entities.SourceApiModel;
import org.marsik.elshelves.api.entities.TransactionApiModel;
import org.marsik.elshelves.api.entities.UserApiModel;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.entities.Document;
import org.marsik.elshelves.backend.entities.Footprint;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.IdentifiedEntityInterface;
import org.marsik.elshelves.backend.entities.Item;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.LotHistory;
import org.marsik.elshelves.backend.entities.NamedEntity;
import org.marsik.elshelves.backend.entities.NumericProperty;
import org.marsik.elshelves.backend.entities.NumericPropertyValue;
import org.marsik.elshelves.backend.entities.OwnedEntityInterface;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.Transaction;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.Unit;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.BoxToEmber;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.entities.converters.CodeToEmber;
import org.marsik.elshelves.backend.entities.converters.DocumentToEmber;
import org.marsik.elshelves.backend.entities.converters.EmberToBox;
import org.marsik.elshelves.backend.entities.converters.EmberToCode;
import org.marsik.elshelves.backend.entities.converters.EmberToDocument;
import org.marsik.elshelves.backend.entities.converters.EmberToFootprint;
import org.marsik.elshelves.backend.entities.converters.EmberToGroup;
import org.marsik.elshelves.backend.entities.converters.EmberToItem;
import org.marsik.elshelves.backend.entities.converters.EmberToList;
import org.marsik.elshelves.backend.entities.converters.EmberToLot;
import org.marsik.elshelves.backend.entities.converters.EmberToLotHistory;
import org.marsik.elshelves.backend.entities.converters.EmberToNumericProperty;
import org.marsik.elshelves.backend.entities.converters.EmberToPurchase;
import org.marsik.elshelves.backend.entities.converters.EmberToRequirement;
import org.marsik.elshelves.backend.entities.converters.EmberToSource;
import org.marsik.elshelves.backend.entities.converters.EmberToTransaction;
import org.marsik.elshelves.backend.entities.converters.EmberToType;
import org.marsik.elshelves.backend.entities.converters.EmberToUnit;
import org.marsik.elshelves.backend.entities.converters.FootprintToEmber;
import org.marsik.elshelves.backend.entities.converters.GroupToEmber;
import org.marsik.elshelves.backend.entities.converters.ItemToEmber;
import org.marsik.elshelves.backend.entities.converters.ListToEmber;
import org.marsik.elshelves.backend.entities.converters.LotHistoryToEmber;
import org.marsik.elshelves.backend.entities.converters.LotToEmber;
import org.marsik.elshelves.backend.entities.converters.NumericPropertyToEmber;
import org.marsik.elshelves.backend.entities.converters.PurchaseToEmber;
import org.marsik.elshelves.backend.entities.converters.RequirementToEmber;
import org.marsik.elshelves.backend.entities.converters.SourceToEmber;
import org.marsik.elshelves.backend.entities.converters.TransactionToEmber;
import org.marsik.elshelves.backend.entities.converters.TypeToEmber;
import org.marsik.elshelves.backend.entities.converters.UnitToEmber;
import org.marsik.elshelves.backend.entities.converters.UserToEmber;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.marsik.elshelves.backend.repositories.CodeRepository;
import org.marsik.elshelves.backend.repositories.DocumentRepository;
import org.marsik.elshelves.backend.repositories.FootprintRepository;
import org.marsik.elshelves.backend.repositories.GroupRepository;
import org.marsik.elshelves.backend.repositories.IdentifiedEntityRepository;
import org.marsik.elshelves.backend.repositories.ItemRepository;
import org.marsik.elshelves.backend.repositories.ListRepository;
import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.backend.repositories.NumericPropertyRepository;
import org.marsik.elshelves.backend.repositories.OwnedEntityRepository;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.marsik.elshelves.backend.repositories.RequirementRepository;
import org.marsik.elshelves.backend.repositories.SourceRepository;
import org.marsik.elshelves.backend.repositories.TransactionRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.marsik.elshelves.backend.repositories.UnitRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class BackupService {
    private static final Logger log = LoggerFactory.getLogger(BackupService.class);

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UuidGenerator uuidGenerator;

	@Autowired
    OwnedEntityRepository ownedEntityRepository;

    @Autowired
    IdentifiedEntityRepository identifiedEntityRepository;

	@Autowired
	RelinkService relinkService;

    @Autowired
    EmberToDocument emberToDocument;

	@Autowired
	EmberToLot emberToLot;

    @Autowired
    EmberToLotHistory emberToLotHistory;

    @Autowired
    LotHistoryToEmber lotHistoryToEmber;

	@Autowired
	EmberToPurchase emberToPurchase;

	@Autowired
	EmberToType emberToType;

	@Autowired
	EmberToSource emberToSource;

	@Autowired
	EmberToBox emberToBox;

	@Autowired
	EmberToGroup emberToGroup;

	@Autowired
	EmberToTransaction emberToTransaction;

	@Autowired
	EmberToFootprint emberToFootprint;

	@Autowired
	EmberToRequirement emberToRequirement;

	@Autowired
    EmberToItem emberToItem;

    @Autowired
    EmberToUnit emberToUnit;

    @Autowired
    EmberToNumericProperty emberToNumericProperty;


    @Autowired
    BoxToEmber boxToEmber;

    @Autowired
    DocumentToEmber documentToEmber;

    @Autowired
    FootprintToEmber footprintToEmber;

    @Autowired
    GroupToEmber groupToEmber;

    @Autowired
    LotToEmber lotToEmber;

    @Autowired
    ItemToEmber itemToEmber;

    @Autowired
    PurchaseToEmber purchaseToEmber;

    @Autowired
    RequirementToEmber requirementToEmber;

    @Autowired
    SourceToEmber sourceToEmber;

    @Autowired
    TransactionToEmber transactionToEmber;

    @Autowired
    TypeToEmber typeToEmber;

    @Autowired
    UserToEmber userToEmber;

    @Autowired
    NumericPropertyToEmber numericPropertyToEmber;

    @Autowired
    UnitToEmber unitToEmber;


    @Autowired
    BoxRepository boxRepository;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    FootprintRepository footprintRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    LotRepository lotRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    PurchaseRepository purchaseRepository;

    @Autowired
    RequirementRepository requirementRepository;

    @Autowired
    SourceRepository sourceRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    TypeRepository typeRepository;

    @Autowired
    UnitRepository unitRepository;

    @Autowired
    NumericPropertyRepository numericPropertyRepository;

    @Autowired
    CodeToEmber codeToEmber;

    @Autowired
    EmberToCode emberToCode;

    @Autowired
    CodeRepository codeRepository;

    @Autowired
    ListRepository listRepository;

    @Autowired
    ListToEmber listToEmber;

    @Autowired
    EmberToList emberToList;

	protected <F extends IdentifiedEntity>  void relink(Set<F> allItems,
                                                        User currentUser,
                                                        RelinkService.RelinkContext relinkContext) {
		if (allItems == null) {
			return;
		}

        List<F> fixedItems = new ArrayList<>();

        for (F i: allItems) {
            F fixed = relinkContext.fixUuid(i);
            relinkContext.addToCache(fixed);
            fixedItems.add(fixed);

            assert relinkContext.get(i.getId()) == relinkContext.get(fixed.getId());
            assert relinkContext.get(fixed.getId()) == fixed;
        }

        allItems.clear();
        allItems.addAll(fixedItems);

		for (F i: allItems) {
            log.debug("Relinking {} id {}", i.getClass().getName(), i.getId());
			i.relink(relinkContext);
		}
	}

    protected <F extends IdentifiedEntity>  void save(Iterable<F> allItems) {
        if (allItems == null) {
            return;
        }

        /* Clear all property values so we can save them after
           all referenced entities are saved first.

           This is needed to prevent an exception like:

           org.hibernate.TransientPropertyValueException:
           Not-null property references a transient value
           - transient instance must be saved before current operation :
           NumericPropertyValue.property -> NumericProperty
         */
        List<NumericPropertyValue> propertyValues = new ArrayList<>();
        for (IdentifiedEntity e0: allItems) {
            if (e0 instanceof NamedEntity) {
                propertyValues.addAll(((NamedEntity) e0).getProperties());
                ((NamedEntity) e0).setProperties(new THashSet<>());
            }
        }

        // Create by type map so we can save some specific types first
        Map<Class<?>, Set<F>> byType = new THashMap<>();
        for (F i : allItems) {
            byType.putIfAbsent(i.getClass(), new THashSet<>());
            byType.get(i.getClass()).add(i);
        }

        for (F i : byType.get(Unit.class)) {
            saveOne(i);
        }

        for (F i : byType.get(Type.class)) {
            saveOne(i);
        }

        for (F i : byType.get(NumericProperty.class)) {
            saveOne(i);
        }

        for (F i : byType.get(Footprint.class)) {
            saveOne(i);
        }

        for (F i : byType.get(LotHistory.class)) {
            saveOne(i);
        }

        for (F i : byType.get(Source.class)) {
            saveOne(i);
        }

        for (F i : byType.get(Transaction.class)) {
            saveOne(i);
        }

        for (F i : byType.get(Purchase.class)) {
            saveOne(i);
        }

        for (F i : byType.get(Item.class)) {
            saveOne(i);
        }

        for (Set<F> is : byType.values()) {
            for (F i: is) {
                saveOne(i);
            }
        }

        for (NumericPropertyValue v: propertyValues) {
            entityManager.persist(v);
        }
    }

    private <F extends IdentifiedEntity> void saveOne(F i) {
        // Skip already saved entities
        if (!i.isNew()
                || identifiedEntityRepository.findById(i.getId()) != null) {
            log.debug("Skipping already saved {} id {}", i.getClass().getName(), i.getId());
            return;
        }

        log.debug("Saving {} id {}", i.getClass().getName(), i.getId());
        entityManager.persist(i);
    }

    protected <T, F extends IdentifiedEntity>  void prepare(Iterable<T> items,
                                                       CachingConverter<T, F, UUID> converter,
                                                       User currentUser,
                                                       Map<UUID, Object> conversionCache,
                                                       RelinkService.RelinkContext relinkContext,
                                                       Set<IdentifiedEntity> pool) {
        if (items == null) {
            return;
        }

        for (T i0: items) {
            F i = converter.convert(i0, conversionCache);

            if (i instanceof OwnedEntityInterface) {
                relinkContext.fixOwner((OwnedEntityInterface)i, currentUser);
            }

            pool.add(i);
        }
    }

    @Transactional
	public boolean restoreFromBackup(RestoreApiModel backup,
									 User currentUser) {
		Map<UUID, Object> conversionCache = new THashMap<>();
        RelinkService.RelinkContext relinkContext = relinkService.newRelinker().currentUser(currentUser);
        Set<IdentifiedEntity> pool = new LinkedHashSet<>();

        // Everything will be owned by the current user
        if (backup.getUser() != null
            && backup.getUser().getId() != null) {
            relinkContext.addToCache(backup.getUser().getId(), currentUser);
        }

        // separate lots and lot history
        separateLotsFromHistory(backup.getLots(), backup);

        // convert projects to types + items
        convertProjects(backup.getProjects(), backup, currentUser);

        prepare(backup.getUnits(), emberToUnit, currentUser, conversionCache, relinkContext, pool);
        prepare(backup.getDocuments(), emberToDocument, currentUser, conversionCache, relinkContext, pool);
        prepare(backup.getProperties(), emberToNumericProperty, currentUser, conversionCache, relinkContext, pool);
        prepare(backup.getBoxes(), emberToBox, currentUser, conversionCache, relinkContext, pool);
        prepare(backup.getGroups(), emberToGroup, currentUser, conversionCache, relinkContext, pool);
        prepare(backup.getFootprints(), emberToFootprint, currentUser, conversionCache, relinkContext, pool);
        prepare(backup.getTypes(), emberToType, currentUser, conversionCache, relinkContext, pool);
        prepare(backup.getSources(), emberToSource, currentUser, conversionCache, relinkContext, pool);
        prepare(backup.getTransactions(), emberToTransaction, currentUser, conversionCache, relinkContext, pool);
        prepare(backup.getPurchases(), emberToPurchase, currentUser, conversionCache, relinkContext, pool);
        prepare(backup.getLots(), emberToLot, currentUser, conversionCache, relinkContext, pool);
        prepare(backup.getItems(), emberToItem, currentUser, conversionCache, relinkContext, pool);
        prepare(backup.getRequirements(), emberToRequirement, currentUser, conversionCache, relinkContext, pool);
        prepare(backup.getHistory(), emberToLotHistory, currentUser, conversionCache, relinkContext, pool);
        prepare(backup.getCodes(), emberToCode, currentUser, conversionCache, relinkContext, pool);
        prepare(backup.getLists(), emberToList, currentUser, conversionCache, relinkContext, pool);

        relink(pool, currentUser, relinkContext);

        upgradeTransactions(pool, relinkContext);
        upgradeEntities(pool, relinkContext);
        upgradeDocuments(pool, relinkContext);
        upgradePurchases(pool, relinkContext);

        save(pool);

		return true;
	}

    private void separateLotsFromHistory(Set<LotApiModel> lots, RestoreApiModel backup) {
        for (Iterator<LotApiModel> it = lots.iterator(); it.hasNext(); ) {
            LotApiModel lot = it.next();

            if (lot.getHistory() != null && lot.getPrevious() == null
                    && (lot.getNext() == null || lot.getNext().isEmpty())) {
                // Proper new style Lot
                continue;
            }

            if (lot.getCreated() == null) {
                lot.setCreated(new DateTime());
            }

            if (lot.getNext() != null && !lot.getNext().isEmpty()) {
                // Remove history object from lots
                it.remove();

                // Create a lot history record
                LotHistoryApiModel history = new LotHistoryApiModel();
                history.setAction(lot.getAction());
                history.setId(lot.getId());
                history.setValidSince(lot.getCreated());

                if (lot.getPerformedBy() != null) {
                    history.setPerformedBy(new UserApiModel());
                    history.getPerformedBy().setId(lot.getPerformedBy().getId());
                    history.getPerformedBy().setStub(true);
                }
                if (lot.getLocation() != null) {
                    history.setLocation(new BoxApiModel());
                    history.getLocation().setId(lot.getLocation().getId());
                    history.getLocation().setStub(true);
                }

                if (lot.getPrevious() != null) {
                    history.setPrevious(new LotHistoryApiModel());
                    history.getPrevious().setId(lot.getPrevious().getId());
                    history.getPrevious().setStub(true);
                }

                backup.getHistory().add(history);
            } else {
                // Real lot object
                if (lot.getAction() == LotAction.SPLIT) {
                    // Use the last lot as history when the last operation was split
                    // it will be relinked to the proper history object
                    lot.setHistory(new LotHistoryApiModel());
                    lot.getHistory().setId(lot.getPrevious().getId());
                    lot.getHistory().setStub(true);

                } else {
                    // Prepare a history record
                    LotHistoryApiModel history = new LotHistoryApiModel();
                    history.setAction(lot.getAction());
                    history.setId(uuidGenerator.generate());
                    history.setValidSince(lot.getCreated());

                    if (lot.getPerformedBy() != null) {
                        history.setPerformedBy(new UserApiModel());
                        history.getPerformedBy().setId(lot.getPerformedBy().getId());
                        history.getPerformedBy().setStub(true);
                    }

                    if (lot.getLocation() != null) {
                        history.setLocation(new BoxApiModel());
                        history.getLocation().setId(lot.getLocation().getId());
                        history.getLocation().setStub(true);
                    }

                    if (lot.getPrevious() != null) {
                        history.setPrevious(new LotHistoryApiModel());
                        history.getPrevious().setId(lot.getPrevious().getId());
                        history.getPrevious().setStub(true);
                    }

                    lot.setHistory(history);
                    backup.getHistory().add(history);
                }
            }
        }
    }

    private void convertProjects(Set<ProjectApiModel> projects, RestoreApiModel backup, User currentUser) {
        LotHistoryApiModel history = new LotHistoryApiModel();
        history.setId(uuidGenerator.generate());
        history.setAction(LotAction.DELIVERY);
        history.setValidSince(new DateTime());
        backup.getHistory().add(history);

        SourceApiModel source = new SourceApiModel();

        if (currentUser.getProjectSource() != null) {
            source.setId(currentUser.getProjectSource().getId());
            source.setStub(true);
        } else {
            source.setName("Backup restore " + new DateTime().toString());
            source.setId(uuidGenerator.generate());
            source.setDescribedBy(new THashSet<>());
            source.setCodes(new THashSet<>());
            source.setProperties(new THashSet<>());
            backup.getSources().add(source);
        }

        PartGroupApiModel g = new PartGroupApiModel();
        g.setId(uuidGenerator.generate());
        g.setName("Imported projects");
        g.setTypes(new THashSet<>());
        g.setGroups(new THashSet<>());
        g.setShowProperties(new THashSet<>());
        g.setDescribedBy(new THashSet<>());
        g.setProperties(new THashSet<>());
        g.setCodes(new THashSet<>());
        backup.getGroups().add(g);

        for (ProjectApiModel project: projects) {
            PartTypeApiModel type = modelMapper.map(project, PartTypeApiModel.class);
            type.setManufacturable(true);
            type.setGroups(new THashSet<>());
            type.getGroups().add(g);
            type.setFootprints(new THashSet<>());
            type.setDescribedBy(new THashSet<>());
            type.setProperties(new THashSet<>());
            type.setCodes(new THashSet<>());
            type.setSeeAlso(new THashSet<>());
            backup.getTypes().add(type);

            ItemApiModel item = modelMapper.map(project, ItemApiModel.class);
            item.setId(uuidGenerator.generate());
            item.setCount(1L);
            item.setFinished(false);
            item.setHistory(history);
            item.setType(type);
            item.setStatus(LotAction.DELIVERY);
            item.setRequirements(project.getRequirements());
            backup.getItems().add(item);

            type.setLots(new THashSet<>());
            type.getLots().add(PolymorphicRecord.build(item));

            PurchaseApiModel purchase = new PurchaseApiModel();
            purchase.setId(uuidGenerator.generate());
            purchase.setLots(new THashSet<>());
            purchase.getLots().add(PolymorphicRecord.build(item));
            purchase.setCount(1L);
            purchase.setType(type);
            backup.getPurchases().add(purchase);

            TransactionApiModel transaction = new TransactionApiModel();
            transaction.setId(uuidGenerator.generate());
            transaction.setItems(new THashSet<>());
            transaction.getItems().add(purchase);
            transaction.setName(project.getName());
            transaction.setDate(new DateTime());
            transaction.setSource(source);
            transaction.setDescribedBy(new THashSet<>());
            transaction.setCodes(new THashSet<>());
            transaction.setProperties(new THashSet<>());
            backup.getTransactions().add(transaction);
        }
    }

    private void upgradeEntities(Set<? extends IdentifiedEntityInterface> pool, RelinkService.RelinkContext relinkCache) {
        for (IdentifiedEntityInterface d0 : pool) {
            if (!(d0 instanceof NamedEntity)) {
                continue;
            }

            NamedEntity d = (NamedEntity) d0;

            if (d.getName() == null
                    || d.getName().isEmpty()) {
                d.setName("unknown");
            }
        }
    }

    /**
     * Make sure all the required fields are present. Old versions made it possible to delete name
     * or content-type.
     *
     * @param pool Objects that are to be saved and might contain Document instances
     * @param relinkCache Relink cache that already contains all document instances
     */
    private void upgradeDocuments(Set<? extends IdentifiedEntityInterface> pool, RelinkService.RelinkContext relinkCache) {
       for (IdentifiedEntityInterface d0: pool) {
           if (!(d0 instanceof Document)) {
               continue;
           }

           Document d = (Document)d0;

           if (d.getName() == null
                   || d.getName().isEmpty()) {
               if (d.getUrl() != null
                       && d.getUrl().getPath() != null
                       && !d.getUrl().getPath().isEmpty()) {
                   final Path fileName = Paths.get(d.getUrl().getPath()).getFileName();
                   if (fileName != null) {
                       d.setName(fileName.toString());
                   } else {
                       d.setName("unknown");
                   }
               } else {
                   d.setName("unknown");
               }
           }

           if (d.getContentType() == null
                   || d.getContentType().isEmpty()) {
               d.setContentType("application/octet-stream");
           }

           if (d.getSize() == null) {
               d.setSize(0L);
           }

           if (d.getCreated() == null) {
               d.setCreated(new DateTime());
           }
       }
    }

    /**
     * Make sure transactions have name. It became necessary and old backups might still not have it
     * @param pool list of entities to be saved that might contain Transaction instances
     * @param relinkCache relink cache that has to contain all the transactions and sources
     */
    private void upgradeTransactions(Set<? extends IdentifiedEntityInterface> pool, RelinkService.RelinkContext relinkCache) {
        int seq = 0;

        for (IdentifiedEntityInterface t0: pool) {
            if (!(t0 instanceof Transaction)) {
                continue;
            }

            Transaction t = (Transaction)t0;
            if (t.getName() != null
                    && !t.getName().isEmpty()) {
                continue;
            }

            Source source = t.getSource();
            t.setName(source.getName() + " "
                    + (t.getDate() != null ? t.getDate().toString() : Integer.toString(seq++)));
        }
    }

    /**
     * Make sure Purchases have created time
     * @param pool list of entities to be saved that might contain Purchase instances
     * @param relinkCache relink cache that has to contain all the purchases and lots
     */
    private void upgradePurchases(Set<? extends IdentifiedEntityInterface> pool, RelinkService.RelinkContext relinkCache) {
        int seq = 0;

        for (IdentifiedEntityInterface p0 : pool) {
            if (!(p0 instanceof Purchase)) {
                continue;
            }

            Purchase p = (Purchase) p0;
            if (p.getCreated() != null) {
                continue;
            } else if (p.getLots().isEmpty()) {
                p.setCreated(new DateTime());
                continue;
            }

            Lot source = p.getLots().iterator().next();
            p.setCreated(source.getCreated());
        }
    }

    protected <T extends IdentifiedEntity, E extends AbstractEntityApiModel, R extends JpaRepository<T, UUID>> Set<E> backup(Iterable<T> items,
                                                                                                                    CachingConverter<T, E, UUID> convertor,
                                                                                                                    Map<UUID, Object> cache) {
        Set<E> dtos = new THashSet<>();
        for (T item: items) {
            if (cache.containsKey(item.getId())) {
                continue;
            }
            dtos.add(convertor.convert(item, cache));
        }

        return dtos;
    }

    @Transactional(readOnly = true)
	public RestoreApiModel doBackup(User currentUser) {
        RestoreApiModel backup = new RestoreApiModel();
        Map<UUID, Object> cache = new THashMap<>();

        backup.setUnits(backup(unitRepository.findByOwner(currentUser), unitToEmber, cache));
        backup.setDocuments(backup(documentRepository.findByOwner(currentUser), documentToEmber, cache));
        backup.setProperties(backup(numericPropertyRepository.findByOwner(currentUser), numericPropertyToEmber, cache));
        backup.setBoxes(backup(boxRepository.findByOwner(currentUser), boxToEmber, cache));
        backup.setGroups(backup(groupRepository.findByOwner(currentUser), groupToEmber, cache));
        backup.setFootprints(backup(footprintRepository.findByOwner(currentUser), footprintToEmber, cache));
        backup.setTypes(backup(typeRepository.findByOwner(currentUser), typeToEmber, cache));
        backup.setCodes(backup(codeRepository.findByOwner(currentUser), codeToEmber, cache));

        backup.setSources(backup(sourceRepository.findByOwner(currentUser), sourceToEmber, cache));
        backup.setTransactions(backup(transactionRepository.findByOwner(currentUser), transactionToEmber, cache));
        backup.setPurchases(backup(purchaseRepository.findByTransactionOwner(currentUser), purchaseToEmber, cache));

        // Items have to be loaded before Lots
        Collection<Item> items = itemRepository.findByOwner(currentUser);
        final Collection<Lot> lots = lotRepository.findByOwner(currentUser);

        backup.setItems(backup(items, itemToEmber, cache));
        backup.setLots(backup(lots, lotToEmber, cache));

        backup.setRequirements(backup(requirementRepository.findByItemOwner(currentUser), requirementToEmber, cache));
        backup.setUser(userToEmber.convert(currentUser, cache));

        Set<LotHistory> history = new THashSet<>();

        for (Lot l: items) {
            collectHistory(history, l);
        }

        for (Lot l: lots) {
            collectHistory(history, l);
        }

        backup.setHistory(new THashSet<>());

        for (LotHistory h: history) {
            backup.getHistory().add(lotHistoryToEmber.convert(h, cache));
        }

        backup.setLists(backup(listRepository.findByOwner(currentUser), listToEmber, cache));

        return backup;
	}

    private void collectHistory(Set<LotHistory> history, Lot l) {
        LotHistory h = l.getHistory();
        while (h != null) {
            history.add(h);
            h = h.getPrevious();
        }
    }
}
