package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.api.entities.NumericPropertyApiModel;

public class NumericPropertyIdResolver extends AbstractIdResolver {
    @Override
    protected Class<? extends AbstractEntityApiModel> getType() {
        return NumericPropertyApiModel.class;
    }
}
