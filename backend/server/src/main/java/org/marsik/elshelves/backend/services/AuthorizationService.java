package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.entities.Authorization;
import org.marsik.elshelves.backend.repositories.AuthorizationRepository;

public interface AuthorizationService
        extends AbstractRestServiceIntf<AuthorizationRepository, Authorization> {
}
