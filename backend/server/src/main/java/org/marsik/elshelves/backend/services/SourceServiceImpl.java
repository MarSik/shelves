package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.SourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Service
public class SourceServiceImpl extends AbstractRestService<SourceRepository, Source> implements SourceService {
	@Autowired
	StorageManager storageManager;

	@Autowired
	public SourceServiceImpl(SourceRepository repository,
			UuidGenerator uuidGenerator) {
		super(repository, uuidGenerator);
	}

	@Override
	public Source get(UUID uuid, User currentUser) throws PermissionDenied, EntityNotFound {
		Source s = super.get(uuid, currentUser);
		try {
			s.setHasIcon(storageManager.exists(uuid));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return s;
	}

	@Override
	public void deleteEntity(Source entity) throws OperationNotPermitted {
		if (!entity.canBeDeleted()) {
			throw new OperationNotPermitted();
		}

		super.deleteEntity(entity);
	}

    @Override
    protected Iterable<Source> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }

	@Async
	protected void refreshFavicon(UUID uuid) {
		Source s = getRepository().findById(uuid);

		if (s.getUrl() == null
				|| s.getUrl().isEmpty()) {
			return;
		}

		URL baseurl;
		URL faviconUrl;

		try {
			baseurl = new URL(s.getUrl());
			faviconUrl = new URL(baseurl, "/favicon.ico");
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
			return;
		}

		try {
			storageManager.download(uuid, faviconUrl, null);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public Source create(Source dto, User currentUser) throws OperationNotPermitted {
		Source s = super.create(dto, currentUser);
		if (s != null && s.getId() != null) {
			refreshFavicon(s.getId());
		}
		return s;
	}

	@Override
	public Source update(Source dto, User currentUser) throws BaseRestException {
		Source s = super.update(dto, currentUser);
		if (s != null && s.getId() != null) {
			refreshFavicon(s.getId());
		}
		return s;
	}

	@Override
	public boolean delete(UUID uuid, User currentUser) throws BaseRestException {
		boolean res = super.delete(uuid, currentUser);
		if (res) {
			try {
				storageManager.delete(uuid);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return res;
	}
}
