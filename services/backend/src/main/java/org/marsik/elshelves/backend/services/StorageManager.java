package org.marsik.elshelves.backend.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public interface StorageManager {
    InputStream retrieve(UUID uuid) throws FileNotFoundException;

    OutputStream store(UUID uuid) throws IOException;
}
