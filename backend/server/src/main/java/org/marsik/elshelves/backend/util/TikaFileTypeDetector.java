package org.marsik.elshelves.backend.util;

import org.apache.tika.Tika;

import java.io.IOException;
import java.nio.file.Path;

public class TikaFileTypeDetector extends java.nio.file.spi.FileTypeDetector {
	private final Tika tika = new Tika();

	@Override
	public String probeContentType(Path path) throws IOException {
		return tika.detect(path.toFile());
	}
}
