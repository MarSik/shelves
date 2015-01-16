package org.marsik.elshelves.backend.services;

import java.util.UUID;

public interface StickerCapable {
	UUID getUuid();
	String getName();
	String getSummary();
	String getBaseUrl();
}
