package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.repositories.RequirementRepository;

public interface RequirementService extends AbstractRestServiceIntf<RequirementRepository, Requirement> {
    void deleteEntity(Requirement entity) throws OperationNotPermitted;
}
