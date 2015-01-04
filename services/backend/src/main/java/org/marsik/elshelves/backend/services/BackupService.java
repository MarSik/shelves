package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.BackupApiModel;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;

@Service
public class BackupService {
	@Autowired
	Neo4jTemplate neo4jTemplate;

	@Autowired
	RelinkService relinkService;

	public boolean restoreFromBackup(BackupApiModel backup,
									 User currentUser) {
		return false;
	}

	public BackupApiModel doBackup(User currentUser) {
		return new BackupApiModel();
	}
}
