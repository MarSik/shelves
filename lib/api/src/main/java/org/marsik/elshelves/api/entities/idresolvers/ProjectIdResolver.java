package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.api.entities.ProjectApiModel;

public class ProjectIdResolver extends AbstractIdResolver {
	@Override
	protected Class<? extends AbstractEntityApiModel> getType() {
		return ProjectApiModel.class;
	}
}
