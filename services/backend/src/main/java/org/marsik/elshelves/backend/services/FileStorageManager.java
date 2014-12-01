package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.configuration.StorageConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class FileStorageManager implements StorageManager {
    final StorageConfiguration storageConfiguration;

    public FileStorageManager(StorageConfiguration storageConfiguration) {
        this.storageConfiguration = storageConfiguration;
    }

    private String getPath(UUID uuid) {
        StringBuilder sb = new StringBuilder();
        sb.append(storageConfiguration.getDocumentPath());
        sb.append("/");
        sb.append(uuid.toString().substring(0, 2));
        sb.append("/");
        sb.append(uuid.toString());
        return sb.toString();
    }

    @Override
    public InputStream retrieve(UUID uuid) throws FileNotFoundException {
        File f = new File(getPath(uuid));
        InputStream os = new FileInputStream(f);
        return os;
    }

    @Override
    public OutputStream store(UUID uuid) throws IOException {
        File f = new File(getPath(uuid));
        f.createNewFile();
        return new FileOutputStream(f);
    }
}
