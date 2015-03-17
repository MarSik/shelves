package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.AuthorizationApiModel;

public class AuthorizationIdResolver extends AbstractIdResolver {
    @Override
    protected Class<?> getType() {
        return AuthorizationApiModel.class;
    }
}
