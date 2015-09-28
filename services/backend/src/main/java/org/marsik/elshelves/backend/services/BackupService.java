package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.api.entities.ItemApiModel;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.LotHistoryApiModel;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.api.entities.ProjectApiModel;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.api.entities.RestoreApiModel;
import org.marsik.elshelves.api.entities.SourceApiModel;
import org.marsik.elshelves.api.entities.TransactionApiModel;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.entities.Document;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.IdentifiedEntityInterface;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.NamedEntity;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.OwnedEntityInterface;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.Transaction;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.BoxToEmber;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.entities.converters.DocumentToEmber;
import org.marsik.elshelves.backend.entities.converters.EmberToBox;
import org.marsik.elshelves.backend.entities.converters.EmberToDocument;
import org.marsik.elshelves.backend.entities.converters.EmberToFootprint;
import org.marsik.elshelves.backend.entities.converters.EmberToGroup;
import org.marsik.elshelves.backend.entities.converters.EmberToLot;
import org.marsik.elshelves.backend.entities.converters.EmberToNumericProperty;
import org.marsik.elshelves.backend.entities.converters.EmberToItem;
import org.marsik.elshelves.backend.entities.converters.EmberToPurchase;
import org.marsik.elshelves.backend.entities.converters.EmberToRequirement;
import org.marsik.elshelves.backend.entities.converters.EmberToSource;
import org.marsik.elshelves.backend.entities.converters.EmberToTransaction;
import org.marsik.elshelves.backend.entities.converters.EmberToType;
import org.marsik.elshelves.backend.entities.converters.EmberToUnit;
import org.marsik.elshelves.backend.entities.converters.FootprintToEmber;
import org.marsik.elshelves.backend.entities.converters.GroupToEmber;
import org.marsik.elshelves.backend.entities.converters.LotToEmber;
import org.marsik.elshelves.backend.entities.converters.NumericPropertyToEmber;
import org.marsik.elshelves.backend.entities.converters.ItemToEmber;
import org.marsik.elshelves.backend.entities.converters.PurchaseToEmber;
import org.marsik.elshelves.backend.entities.converters.RequirementToEmber;
import org.marsik.elshelves.backend.entities.converters.SourceToEmber;
import org.marsik.elshelves.backend.entities.converters.TransactionToEmber;
import org.marsik.elshelves.backend.entities.converters.TypeToEmber;
import org.marsik.elshelves.backend.entities.converters.UnitToEmber;
import org.marsik.elshelves.backend.entities.converters.UserToEmber;
import org.marsik.elshelves.backend.repositories.BaseIdentifiedEntityRepository;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.marsik.elshelves.backend.repositories.DocumentRepository;
import org.marsik.elshelves.backend.repositories.FootprintRepository;
import org.marsik.elshelves.backend.repositories.GroupRepository;
import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.backend.repositories.NumericPropertyRepository;
import org.marsik.elshelves.backend.repositories.OwnedEntityRepository;
import org.marsik.elshelves.backend.repositories.ItemRepository;
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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class BackupService {
    private static final Logger log = LoggerFactory.getLogger(BackupService.class);

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UuidGenerator uuidGenerator;

	@Autowired
    OwnedEntityRepository ownedEntityRepository;

    @Autowired
    BaseIdentifiedEntityRepository identifiedEntityRepository;

	@Autowired
	RelinkService relinkService;

    @Autowired
    EmberToDocument emberToDocument;

	@Autowired
	EmberToLot emberToLot;

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


	protected <F extends IdentifiedEntityInterface>  void relink(Iterable<F> allItems,
								   User currentUser,
                                   Map<UUID, IdentifiedEntityInterface> relinkCache) {
		if (allItems == null) {
			return;
		}

		for (F i: allItems) {
            log.debug("Relinking {} id {}", i.getClass().getName(), i.getId());
			relinkService.relink(i, currentUser, relinkCache, true);
		}
	}

    protected <F extends IdentifiedEntityInterface>  void save(Iterable<F> allItems,
                                                               User currentUser,
                                                               Map<UUID, IdentifiedEntityInterface> relinkCache) {
        if (allItems == null) {
            return;
        }


        for (F i: allItems) {
            if (!i.isNew()) {
                log.debug("Skipping already saved {} id {}", i.getClass().getName(), i.getId());
                continue;
            }

            log.debug("Saving {} id {}", i.getClass().getName(), i.getId());
            identifiedEntityRepository.save(i);
        }
    }

    protected <T, F extends OwnedEntityInterface>  void prepare(Iterable<T> items,
                                                       CachingConverter<T, F, UUID> converter,
                                                       User currentUser,
                                                       Map<UUID, Object> conversionCache,
                                                       Map<UUID, IdentifiedEntityInterface> relinkCache,
                                                       Set<IdentifiedEntityInterface> pool) {
        if (items == null) {
            return;
        }

        for (T i0: items) {
            F i = converter.convert(i0, Integer.MAX_VALUE, conversionCache);
            i = relinkService.fixUuidAndOwner(i, currentUser, relinkCache);
            pool.add(i);
        }
    }

	public boolean restoreFromBackup(RestoreApiModel backup,
									 User currentUser) {
		Map<UUID, Object> conversionCache = new THashMap<>();
        Map<UUID, IdentifiedEntityInterface> relinkCache = new THashMap<>();
        Set<IdentifiedEntityInterface> pool = new THashSet<>();

        // Everything will be owned by the current user
        if (backup.getUser() != null
            && backup.getUser().getId() != null) {
            relinkCache.put(backup.getUser().getId(), currentUser);
        }

        // TODO separate lots and lothistory
        separateLotsFromHistory(backup.getLots(), backup);

        // convert projects to types + items
        convertProjects(backup.getProjects(), backup);

        prepare(backup.getUnits(), emberToUnit, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getDocuments(), emberToDocument, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getProperties(), emberToNumericProperty, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getBoxes(), emberToBox, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getGroups(), emberToGroup, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getFootprints(), emberToFootprint, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getTypes(), emberToType, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getItems(), emberToItem, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getSources(), emberToSource, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getTransactions(), emberToTransaction, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getPurchases(), emberToPurchase, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getLots(), emberToLot, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getRequirements(), emberToRequirement, currentUser, conversionCache, relinkCache, pool);

        relink(pool, currentUser, relinkCache);

        upgradeTransactions(pool, relinkCache);
        upgradeEntities(pool, relinkCache);
        upgradeDocuments(pool, relinkCache);
        upgradePurchases(pool, relinkCache);

        save(pool, currentUser, relinkCache);

		return true;
	}

    private void separateLotsFromHistory(Set<LotApiModel> lots, RestoreApiModel backup) {
        for (Iterator<LotApiModel> it = lots.iterator(); it.hasNext(); ) {
            LotApiModel lot = it.next();

            if (lot.getHistory() != null && lot.getPrevious() == null && lot.getNext() == null) {
                // Proper new style Lot
                continue;
            }

            if (lot.getNext() != null) {
                // Remove history from lots
                it.remove();
            } else {
                // Relink history to real lots

                if (lot.getAction() != LotAction.SPLIT) {
                    // Use the last lot as history when the last operation was split
                    lot.setHistory(new LotHistoryApiModel());
                    lot.getHistory().setId(lot.getPrevious().getId());
                } else {
                    // Prepare a history record
                    LotHistoryApiModel history = new LotHistoryApiModel();
                    history.setAction(lot.getAction());
                    history.setId(uuidGenerator.generate());
                    history.setCreated(lot.getCreated());

                    if (lot.getPrevious() != null) {
                        history.setPreviousId(lot.getPrevious().getId());
                    }
                }
            }
        }
    }

    private void convertProjects(Set<ProjectApiModel> projects, RestoreApiModel backup) {
        LotHistoryApiModel history = new LotHistoryApiModel();
        history.setId(uuidGenerator.generate());
        history.setAction(LotAction.DELIVERY);
        history.setCreated(new DateTime());
        backup.getHistory().add(history);

        SourceApiModel source = new SourceApiModel();
        source.setName("Backup restore " + new DateTime().toString());
        source.setId(uuidGenerator.generate());
        backup.getSources().add(source);

        for (ProjectApiModel project: projects) {
            PartTypeApiModel type = modelMapper.map(project, PartTypeApiModel.class);
            backup.getTypes().add(type);


            ItemApiModel item = modelMapper.map(project, ItemApiModel.class);
            item.setId(uuidGenerator.generate());
            item.setCount(1L);
            item.setFinished(false);
            item.setHistory(history);
            item.setType(type);
            backup.getItems().add(item);

            type.setLots(new THashSet<>());
            type.getLots().add(item);

            PurchaseApiModel purchase = new PurchaseApiModel();
            purchase.setId(uuidGenerator.generate());
            purchase.setLots(new THashSet<>());
            purchase.getLots().add(item);
            backup.getPurchases().add(purchase);

            TransactionApiModel transaction = new TransactionApiModel();
            transaction.setId(uuidGenerator.generate());
            transaction.setItems(new THashSet<>());
            transaction.getItems().add(purchase);
            transaction.setName(project.getName());
            transaction.setDate(new DateTime());
            transaction.setSource(source);
            backup.getTransactions().add(transaction);
        }
    }

    private void upgradeEntities(Set<IdentifiedEntityInterface> pool, Map<UUID, IdentifiedEntityInterface> relinkCache) {
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
    private void upgradeDocuments(Set<IdentifiedEntityInterface> pool, Map<UUID, IdentifiedEntityInterface> relinkCache) {
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
    private void upgradeTransactions(Set<IdentifiedEntityInterface> pool, Map<UUID, IdentifiedEntityInterface> relinkCache) {
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

            Source source = (Source)relinkCache.get(t.getSource().getId());
            t.setName(source.getName() + " "
                    + (t.getDate() != null ? t.getDate().toString() : Integer.toString(seq++)));
        }
    }

    /**
     * Make sure Purchases have created time
     * @param pool list of entities to be saved that might contain Purchase instances
     * @param relinkCache relink cache that has to contain all the purchases and lots
     */
    private void upgradePurchases(Set<IdentifiedEntityInterface> pool, Map<UUID, IdentifiedEntityInterface> relinkCache) {
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

            Lot source = (Lot)relinkCache.get(p.getLots().iterator().next().getId());
            p.setCreated(source.getLastModified());
        }
    }

    protected <T extends OwnedEntity, E extends AbstractEntityApiModel, R extends JpaRepository<T, UUID>> Set<E> backup(Iterable<T> items,
                                                                                                                    CachingConverter<T, E, UUID> convertor,
                                                                                                                    Map<UUID, Object> cache) {
        Set<E> dtos = new THashSet<>();
        for (T item: items) {
            dtos.add(convertor.convert(item, 1, cache));
        }

        return dtos;
    }

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

        backup.setSources(backup(sourceRepository.findByOwner(currentUser), sourceToEmber, cache));
        backup.setTransactions(backup(transactionRepository.findByOwner(currentUser), transactionToEmber, cache));
        backup.setPurchases(backup(purchaseRepository.findByTransactionOwner(currentUser), purchaseToEmber, cache));

        backup.setLots(backup(lotRepository.findByOwner(currentUser), lotToEmber, cache));
        backup.setItems(backup(itemRepository.findByOwner(currentUser), itemToEmber, cache));

        backup.setRequirements(backup(requirementRepository.findByItemOwner(currentUser), requirementToEmber, cache));
        backup.setUser(userToEmber.convert(currentUser, 1, cache));

        return backup;
	}
}
