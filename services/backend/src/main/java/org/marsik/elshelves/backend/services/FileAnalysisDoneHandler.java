package org.marsik.elshelves.backend.services;

import org.apache.tika.metadata.Metadata;

import java.nio.file.Path;
import java.util.UUID;

public interface FileAnalysisDoneHandler {
	void downloadFinished(UUID uuid, Path path, Long size, String contentType, Metadata metadata);
}
