package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.api.entities.AuthorizationApiModel;

public class AuthorizationIdResolver extends AbstractIdResolver {
    @Override
    protected Class<? extends AbstractEntityApiModel> getType() {
        return AuthorizationApiModel.class;
    }
}
