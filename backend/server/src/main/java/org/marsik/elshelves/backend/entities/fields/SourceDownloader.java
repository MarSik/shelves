package org.marsik.elshelves.backend.entities.fields;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;
import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.backend.services.StorageManager;

import java.util.Map;
import java.util.UUID;

/**
 * Supported implementations of price downloaders
 */
public enum SourceDownloader {
	NONE(null, null),
	FARNELL(null, null),
	DIGIKEY(null, null),
	MOUSER(null, null);

	static interface PriceDownloader {
		Map<String, Float> download(Iterable<String> ids);
		boolean vatIncluded();
	}

	final PriceDownloader priceDownloader;

	public Map<String, Float> getPrices(Iterable<String> ids) {
		return new THashMap<>();
	}

	static interface DataDownloader {
		Map<String, Iterable<UUID>> download(Iterable<String> ids, StorageManager storageManager);
	}

	final DataDownloader dataDownloader;

	public Map<String, Iterable<UUID>> downloadData(Iterable<String> ids, StorageManager storageManager) {
		return new THashMap<>();
	}

	SourceDownloader(PriceDownloader priceDownloader, DataDownloader dataDownloader) {
		this.priceDownloader = priceDownloader;
		this.dataDownloader = dataDownloader;
	}

	public String getId() {
		return name();
	}

	@JsonCreator
	public static SourceDownloader forValue(JsonNode s) {
		if (s.isObject()) {
			return s.get("id") == null ? NONE : SourceDownloader.valueOf(s.get("id").asText());
		} else if (s.isTextual()) {
			return SourceDownloader.valueOf(s.asText());
		} else if (s.isNull()) {
			return null;
		} else if (s.isNumber()) {
			return SourceDownloader.values()[s.asInt()];
		} else {
			return null;
		}
	}
}
