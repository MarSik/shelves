package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.api.entities.ItemApiModel;

public class ItemIdResolver extends AbstractIdResolver {
    @Override
    protected Class<? extends AbstractEntityApiModel> getType() {
        return ItemApiModel.class;
    }
}
