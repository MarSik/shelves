package org.marsik.elshelves.api.entities;

import org.marsik.elshelves.api.ember.EmberModelName;

@EmberModelName("source")
public class SourceApiModel extends AbstractEntityApiModel {
	String name;
	String url;

	UserApiModel belongsTo;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public UserApiModel getBelongsTo() {
		return belongsTo;
	}

	public void setBelongsTo(UserApiModel belongsTo) {
		this.belongsTo = belongsTo;
	}
}
