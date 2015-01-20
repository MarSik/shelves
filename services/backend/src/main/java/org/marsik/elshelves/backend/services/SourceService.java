package org.marsik.elshelves.backend.services;

import org.apache.commons.io.IOUtils;
import org.marsik.elshelves.api.entities.SourceApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToSource;
import org.marsik.elshelves.backend.entities.converters.SourceToEmber;
import org.marsik.elshelves.backend.repositories.SourceRepository;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Service
public class SourceService extends AbstractRestService<SourceRepository, Source, SourceApiModel> {
	@Autowired
	StorageManager storageManager;

	@Autowired
	public SourceService(SourceRepository repository,
						 SourceToEmber dbToRest,
						 EmberToSource restToDb,
						 UuidGenerator uuidGenerator) {
		super(repository, dbToRest, restToDb, uuidGenerator);
	}

	@Override
	public SourceApiModel get(UUID uuid, User currentUser) throws PermissionDenied, EntityNotFound {
		SourceApiModel s = super.get(uuid, currentUser);
		try {
			s.setHasIcon(storageManager.exists(uuid));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return s;
	}

	@Override
	protected void deleteEntity(Source entity) throws OperationNotPermitted {
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
		Source s = getRepository().findByUuid(uuid);

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
	public SourceApiModel create(SourceApiModel dto, User currentUser) throws OperationNotPermitted {
		SourceApiModel s = super.create(dto, currentUser);
		if (s != null && s.getId() != null) {
			refreshFavicon(s.getId());
		}
		return s;
	}

	@Override
	public SourceApiModel update(UUID uuid, SourceApiModel dto, User currentUser) throws PermissionDenied, OperationNotPermitted, EntityNotFound {
		SourceApiModel s = super.update(uuid, dto, currentUser);
		if (s != null && s.getId() != null) {
			refreshFavicon(s.getId());
		}
		return s;
	}

	@Override
	public boolean delete(UUID uuid, User currentUser) throws PermissionDenied, OperationNotPermitted, EntityNotFound {
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
