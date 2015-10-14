package org.marsik.elshelves.backend.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.UUID;

public interface StorageManager {
    InputStream retrieve(UUID uuid) throws FileNotFoundException;

    OutputStream store(UUID uuid) throws IOException;

    void notifyStored(UUID uuid, FileAnalysisDoneHandler finishedHandler) throws IOException;

	void download(UUID uuid, URL url, FileAnalysisDoneHandler finishedHandler) throws IOException;

    void upload(UUID uuid, MultipartFile f, FileAnalysisDoneHandler finishedHandler) throws IOException;

	boolean exists(UUID uuid) throws IOException;

	boolean delete(UUID uuid) throws IOException;

	File get(UUID uuid);
}
