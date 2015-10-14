package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.api.entities.LotHistoryApiModel;

public class LotHistoryIdResolver extends AbstractIdResolver {
    @Override
    protected Class<? extends AbstractEntityApiModel> getType() {
        return LotHistoryApiModel.class;
    }
}
