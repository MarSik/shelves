package org.marsik.elshelves.backend.services;

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

	void download(UUID uuid, URL url) throws IOException;

	boolean exists(UUID uuid) throws IOException;

	boolean delete(UUID uuid) throws IOException;

	File get(UUID uuid);
}
