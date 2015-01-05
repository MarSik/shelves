package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.api.entities.BackupApiModel;
import org.marsik.elshelves.api.entities.BoxApiModel;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.Type;
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
import org.marsik.elshelves.backend.entities.converters.LotToEmber;
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
								   Map<UUID, Object> cache) {
		if (items == null) {
			return;
		}

		for (T i0: items) {
			F i = converter.convert(i0, Integer.MAX_VALUE, cache);
			relinkService.relink(i, currentUser);
			i.setOwner(currentUser);
			neo4jTemplate.save(i);
		}
	}

	public boolean restoreFromBackup(BackupApiModel backup,
									 User currentUser) {
		THashMap<UUID, Object> cache = new THashMap<>();

		restore(backup.getBoxes(), emberToBox, currentUser, cache);
		restore(backup.getGroups(), emberToGroup, currentUser, cache);
		restore(backup.getFootprints(), emberToFootprint, currentUser, cache);
		restore(backup.getTypes(), emberToType, currentUser, cache);
		restore(backup.getProjects(), emberToProject, currentUser, cache);
		restore(backup.getSources(), emberToSource, currentUser, cache);
		restore(backup.getLots(), emberToLot, currentUser, cache);
		restore(backup.getRequirements(), emberToRequirement, currentUser, cache);
		restore(backup.getTransactions(), emberToTransaction, currentUser, cache);
		restore(backup.getPurchases(), emberToPurchase, currentUser, cache);

		return true;
	}

	public BackupApiModel doBackup(User currentUser) {
		return new BackupApiModel();
	}
}
