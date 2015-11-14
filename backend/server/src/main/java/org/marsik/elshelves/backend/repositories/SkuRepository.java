package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Sku;
import org.marsik.elshelves.backend.entities.User;

import java.util.UUID;

public interface SkuRepository extends BaseIdentifiedEntityRepository<Sku> {
    Sku findByTypeOwnerAndSourceIdAndSku(User owner, UUID source, String sku);
}
