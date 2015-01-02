package org.marsik.elshelves.api.entities.deserializers;

import org.marsik.elshelves.api.entities.ProjectApiModel;

public class ProjectIdDeserializer extends EmberIdDeserializer<ProjectApiModel> {
	@Override
	protected Class<? extends ProjectApiModel> getEntityClass() {
		return ProjectApiModel.class;
	}
}
