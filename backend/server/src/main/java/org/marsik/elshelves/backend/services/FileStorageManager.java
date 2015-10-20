package org.marsik.elshelves.backend.services;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class FileStorageManager implements StorageManager, StorageMaintenance {
    final String storagePath;

    final Object hashStoreLock = new Object();

    public FileStorageManager(String storagePath) {
        this.storagePath = storagePath;
    }

    private String getPath(UUID uuid) {
        StringBuilder sb = new StringBuilder();
        sb.append(storagePath);
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
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    public OutputStream store(UUID uuid) throws IOException {
        File f = new File(getPath(uuid));

		f.getParentFile().mkdirs();

        if (!f.createNewFile()) {
            return null;
        }
        return new FileOutputStream(f);
    }

	@Async
    public void notifyStored(UUID uuid, FileAnalysisDoneHandler finishedHandler) throws IOException {
        performHardlinking(uuid);

		if (finishedHandler != null) {
			Path path = get(uuid).toPath();
			String contentType = Files.probeContentType(path);
			Long size = path.toFile().length();
			Metadata metadata;

			try (InputStream stream = new FileInputStream(path.toFile())) {
				AutoDetectParser parser = new AutoDetectParser();
				ContentHandler contentHandler = new BodyContentHandler();
				metadata = new Metadata();
				parser.parse(stream, contentHandler, metadata);
			} catch (SAXException | TikaException ex) {
				ex.printStackTrace();
				metadata = null;
			}

			finishedHandler.downloadFinished(uuid, path, size, contentType, metadata);
		}
    }

	@Override
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
	public void download(UUID uuid, URL url, FileAnalysisDoneHandler finishedHandler) throws IOException {
        File downloadTarget = new File(getPath(uuid) + ".download");

        try {
            FileUtils.copyURLToFile(url, downloadTarget);
            FileUtils.moveFile(downloadTarget, get(uuid));
        } catch (IOException ex) {
            downloadTarget.delete();
            return;
        }

        notifyStored(uuid, finishedHandler);
	}

    @Override
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    public void upload(UUID uuid, MultipartFile file, FileAnalysisDoneHandler finishedHandler) throws IOException {
        File destination = get(uuid);
        destination.getParentFile().mkdirs();
        IOUtils.copy(file.getInputStream(), new FileOutputStream(destination));
        // Can't be used because Jetty does not obey absolute paths..
        // file.transferTo(destination.getAbsoluteFile());

        notifyStored(uuid, finishedHandler);
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
        sb.append(storagePath);
        sb.append("/store/");
        sb.append(hash.substring(0, 2));
        sb.append("/");
        sb.append(hash.substring(2, 4));
        sb.append("/");
        sb.append(hash);
        return new File(sb.toString());
    }

    @Scheduled(cron = "* 15 4 * * *")
    public void performHardLinkMaintenance() {
        // Traverse checksum store and delete unused hardlinks
    }

    @Scheduled(cron = "* 15 3 * * *")
    public void performDownloadMaintenance() {
        // Traverse files and delete stale (more than a day old) downloads
    }

    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
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
