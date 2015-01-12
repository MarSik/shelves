package org.marsik.elshelves.backend.services;

import org.apache.commons.io.FileUtils;
import org.marsik.elshelves.backend.configuration.StorageConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
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

	@Override
	public void download(UUID uuid, URL url) throws IOException {
		FileUtils.copyURLToFile(url, new File(getPath(uuid)));
	}

	@Override
	public boolean exists(UUID uuid) throws IOException {
		File f = new File(getPath(uuid));
		return f.exists();
	}

	@Override
	public boolean delete(UUID uuid) throws IOException {
		File f = new File(getPath(uuid));
		return f.delete();
	}

	@Override
	public File get(UUID uuid) {
		return new File(getPath(uuid));
	}
}
