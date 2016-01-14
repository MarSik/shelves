package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.RestoreApiModel;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by msivak on 1/14/16.
 */
public interface BackupService {
	@Transactional boolean restoreFromBackup(RestoreApiModel backup,
			User currentUser);

	@Transactional(readOnly = true) RestoreApiModel doBackup(User currentUser);
}
