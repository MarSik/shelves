package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.api.entities.BackupApiModel;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.BoxToEmber;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.entities.converters.DocumentToEmber;
import org.marsik.elshelves.backend.entities.converters.EmberToBox;
import org.marsik.elshelves.backend.entities.converters.EmberToFootprint;
import org.marsik.elshelves.backend.entities.converters.EmberToGroup;
import org.marsik.elshelves.backend.entities.converters.EmberToLot;
import org.marsik.elshelves.backend.entities.converters.EmberToNumericProperty;
import org.marsik.elshelves.backend.entities.converters.EmberToProject;
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
import org.marsik.elshelves.backend.entities.converters.ProjectToEmber;
import org.marsik.elshelves.backend.entities.converters.PurchaseToEmber;
import org.marsik.elshelves.backend.entities.converters.RequirementToEmber;
import org.marsik.elshelves.backend.entities.converters.SourceToEmber;
import org.marsik.elshelves.backend.entities.converters.TransactionToEmber;
import org.marsik.elshelves.backend.entities.converters.TypeToEmber;
import org.marsik.elshelves.backend.entities.converters.UnitToEmber;
import org.marsik.elshelves.backend.entities.converters.UserToEmber;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.marsik.elshelves.backend.repositories.DocumentRepository;
import org.marsik.elshelves.backend.repositories.FootprintRepository;
import org.marsik.elshelves.backend.repositories.GroupRepository;
import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.backend.repositories.NumericPropertyRepository;
import org.marsik.elshelves.backend.repositories.OwnedEntityRepository;
import org.marsik.elshelves.backend.repositories.ProjectRepository;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.marsik.elshelves.backend.repositories.RequirementRepository;
import org.marsik.elshelves.backend.repositories.SourceRepository;
import org.marsik.elshelves.backend.repositories.TransactionRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.marsik.elshelves.backend.repositories.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class BackupService {
	@Autowired
    OwnedEntityRepository ownedEntityRepository;

	@Autowired
	RelinkService relinkService;

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
	EmberToProject emberToProject;

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
    ProjectToEmber projectToEmber;

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
    ProjectRepository projectRepository;

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


	protected <F extends OwnedEntity>  void restore(Iterable<F> allItems,
								   User currentUser,
                                   Map<UUID, OwnedEntity> relinkCache) {
		if (allItems == null) {
			return;
		}

		for (F i: allItems) {
			relinkService.relink(i, currentUser, relinkCache, false);
		}

        for (F i: allItems) {
            ownedEntityRepository.save(i);
        }
	}

    protected <T, F extends OwnedEntity>  void prepare(Iterable<T> items,
                                                       CachingConverter<T, F, UUID> converter,
                                                       User currentUser,
                                                       Map<UUID, Object> conversionCache,
                                                       Map<UUID, OwnedEntity> relinkCache,
                                                       Set<OwnedEntity> pool) {
        if (items == null) {
            return;
        }

        for (T i0: items) {
            F i = converter.convert(i0, Integer.MAX_VALUE, conversionCache);
            i = relinkService.fixUuidAndOwner(i, currentUser, relinkCache);
            pool.add(i);
        }
    }

	public boolean restoreFromBackup(BackupApiModel backup,
									 User currentUser) {
		Map<UUID, Object> conversionCache = new THashMap<>();
        Map<UUID, OwnedEntity> relinkCache = new THashMap<>();
        Set<OwnedEntity> pool = new THashSet<>();

        prepare(backup.getUnits(), emberToUnit, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getProperties(), emberToNumericProperty, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getBoxes(), emberToBox, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getGroups(), emberToGroup, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getFootprints(), emberToFootprint, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getTypes(), emberToType, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getProjects(), emberToProject, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getSources(), emberToSource, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getTransactions(), emberToTransaction, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getPurchases(), emberToPurchase, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getLots(), emberToLot, currentUser, conversionCache, relinkCache, pool);
        prepare(backup.getRequirements(), emberToRequirement, currentUser, conversionCache, relinkCache, pool);

        restore(pool, currentUser, relinkCache);

		return true;
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

	public BackupApiModel doBackup(User currentUser) {
        BackupApiModel backup = new BackupApiModel();
        Map<UUID, Object> cache = new THashMap<>();

        backup.setUnits(backup(unitRepository.findByOwner(currentUser), unitToEmber, cache));
        backup.setProperties(backup(numericPropertyRepository.findByOwner(currentUser), numericPropertyToEmber, cache));
        backup.setBoxes(backup(boxRepository.findByOwner(currentUser), boxToEmber, cache));
        backup.setGroups(backup(groupRepository.findByOwner(currentUser), groupToEmber, cache));
        backup.setFootprints(backup(footprintRepository.findByOwner(currentUser), footprintToEmber, cache));
        backup.setTypes(backup(typeRepository.findByOwner(currentUser), typeToEmber, cache));
        backup.setProjects(backup(projectRepository.findByOwner(currentUser), projectToEmber, cache));
        backup.setSources(backup(sourceRepository.findByOwner(currentUser), sourceToEmber, cache));
        backup.setTransactions(backup(transactionRepository.findByOwner(currentUser), transactionToEmber, cache));
        backup.setPurchases(backup(purchaseRepository.findByTransactionOwner(currentUser), purchaseToEmber, cache));
        backup.setLots(backup(lotRepository.findByOwner(currentUser), lotToEmber, cache));
        backup.setRequirements(backup(requirementRepository.findByProjectOwner(currentUser), requirementToEmber, cache));
        backup.setUser(userToEmber.convert(currentUser, 1, cache));

        return backup;
	}
}
