package org.marsik.elshelves.backend.services;

import org.apache.tika.metadata.Metadata;
import org.marsik.elshelves.backend.entities.Document;
import org.marsik.elshelves.backend.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.UUID;

@Service
public class DocumentAnalysisDoneService implements FileAnalysisDoneHandler {
	@Autowired
	DocumentRepository documentRepository;

	@Override
	@Transactional
	public void downloadFinished(UUID uuid, Path path, Long size, String contentType, Metadata metadata) {
		Document d = documentRepository.findByUuid(uuid);
		if (d == null) {
			return;
		}

		d.setContentType(contentType != null ? contentType : d.getContentType());
		d.setSize(size != null ? size : d.getSize());

		if (metadata != null) {
            if (metadata.get(Metadata.TITLE) != null
                    && !metadata.get(Metadata.TITLE).trim().isEmpty()) {
                d.setName(metadata.get(Metadata.TITLE));
            }
		}

        documentRepository.save(d);
	}
}
