package org.marsik.elshelves.backend.services;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.marsik.elshelves.backend.configuration.StorageConfiguration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;

public class FileStorageManager implements StorageManager {
    final StorageConfiguration storageConfiguration;

    final Object hashStoreLock = new Object();

    public FileStorageManager(StorageConfiguration storageConfiguration) {
        this.storageConfiguration = storageConfiguration;
    }

    private String getPath(UUID uuid) {
        StringBuilder sb = new StringBuilder();
        sb.append(storageConfiguration.getDocumentPath());
        sb.append("/");
        sb.append(uuid.toString().substring(0, 2));
        sb.append("/");
        sb.append(uuid.toString().substring(2, 4));
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

    public void notifyStored(UUID uuid) {
        performHardlinking(uuid);
    }

	@Override
	public void download(UUID uuid, URL url) throws IOException {
        File downloadTarget = new File(getPath(uuid) + ".download");

        try {
            FileUtils.copyURLToFile(url, downloadTarget);
            FileUtils.moveFile(downloadTarget, get(uuid));
        } catch (IOException ex) {
            downloadTarget.delete();
            return;
        }

        notifyStored(uuid);
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

    private File getHashFile(String hash) {
        StringBuilder sb = new StringBuilder();
        sb.append(storageConfiguration.getDocumentPath());
        sb.append("/store/");
        sb.append(hash.substring(0, 2));
        sb.append("/");
        sb.append(hash.substring(2, 4));
        sb.append("/");
        sb.append(hash);
        return new File(sb.toString());
    }

    @Scheduled(cron = "* 15 4 * * *")
    private void performHardLinkMaintenance() {
        // Traverse checksum store and delete unused hardlinks
    }

    @Scheduled(cron = "* 15 3 * * *")
    private void performDownloadMaintenance() {
        // Traverse files and delete stale (more than a day old) downloads
    }

    @Async
    private void performHardlinking(UUID uuid) {
        // Compute file checksum
        String hash;
        try {
            hash = DigestUtils.sha256Hex(retrieve(uuid));
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        // Synchronize access to the checksum store
        File hashFile;
        synchronized (hashStoreLock) {
            try {
                // Store into checksum store or get the proper path
                hashFile = getHashFile(hash);
                if (!hashFile.exists()) {
                    FileUtils.copyFile(get(uuid), hashFile);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
        }

        // Delete original file and create hardlink
        synchronized (this) {
            try {
                get(uuid).delete();
                Files.createLink(get(uuid).toPath(), hashFile.toPath());
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
        }
    }
}
