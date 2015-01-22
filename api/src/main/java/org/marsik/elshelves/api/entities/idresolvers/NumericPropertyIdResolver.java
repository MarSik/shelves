package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.NumericPropertyApiModel;

public class NumericPropertyIdResolver extends AbstractIdResolver {
    @Override
    protected Class<?> getType() {
        return NumericPropertyApiModel.class;
    }
}
