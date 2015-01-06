package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.api.entities.BackupApiModel;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.entities.converters.EmberToBox;
import org.marsik.elshelves.backend.entities.converters.EmberToFootprint;
import org.marsik.elshelves.backend.entities.converters.EmberToGroup;
import org.marsik.elshelves.backend.entities.converters.EmberToLot;
import org.marsik.elshelves.backend.entities.converters.EmberToProject;
import org.marsik.elshelves.backend.entities.converters.EmberToPurchase;
import org.marsik.elshelves.backend.entities.converters.EmberToRequirement;
import org.marsik.elshelves.backend.entities.converters.EmberToSource;
import org.marsik.elshelves.backend.entities.converters.EmberToTransaction;
import org.marsik.elshelves.backend.entities.converters.EmberToType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class BackupService {
	@Autowired
	Neo4jTemplate neo4jTemplate;

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

	protected <T, F extends OwnedEntity>  void restore(Iterable<T> items,
								   CachingConverter<T, F, UUID> converter,
								   User currentUser,
								   Map<UUID, Object> conversionCache,
                                   Map<UUID, Object> relinkCache) {
		if (items == null) {
			return;
		}

		for (T i0: items) {
			F i = converter.convert(i0, Integer.MAX_VALUE, conversionCache);
			relinkService.relink(i, currentUser, relinkCache);
			i.setOwner(currentUser);
			neo4jTemplate.save(i);
		}
	}

	public boolean restoreFromBackup(BackupApiModel backup,
									 User currentUser) {
		Map<UUID, Object> conversionCache = new THashMap<>();
        Map<UUID, Object> relinkCache = new THashMap<>();

		restore(backup.getBoxes(), emberToBox, currentUser, conversionCache, relinkCache);
		restore(backup.getGroups(), emberToGroup, currentUser, conversionCache, relinkCache);
		restore(backup.getFootprints(), emberToFootprint, currentUser, conversionCache, relinkCache);
		restore(backup.getTypes(), emberToType, currentUser, conversionCache, relinkCache);
		restore(backup.getProjects(), emberToProject, currentUser, conversionCache, relinkCache);
		restore(backup.getSources(), emberToSource, currentUser, conversionCache, relinkCache);
        restore(backup.getTransactions(), emberToTransaction, currentUser, conversionCache, relinkCache);
        restore(backup.getPurchases(), emberToPurchase, currentUser, conversionCache, relinkCache);
		restore(backup.getLots(), emberToLot, currentUser, conversionCache, relinkCache);
		restore(backup.getRequirements(), emberToRequirement, currentUser, conversionCache, relinkCache);

		return true;
	}

	public BackupApiModel doBackup(User currentUser) {
		return new BackupApiModel();
	}
}
