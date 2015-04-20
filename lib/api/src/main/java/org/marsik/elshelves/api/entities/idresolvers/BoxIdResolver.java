package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.api.entities.BoxApiModel;

public class BoxIdResolver extends AbstractIdResolver {
	@Override
	protected Class<? extends AbstractEntityApiModel> getType() {
		return BoxApiModel.class;
	}
}
