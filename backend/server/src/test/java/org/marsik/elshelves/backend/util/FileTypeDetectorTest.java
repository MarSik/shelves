package org.marsik.elshelves.backend.util;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class FileTypeDetectorTest {
    @Test
    public void testKicadSchematicsMimeType() throws IOException, URISyntaxException {
        Path path = Paths.get(getClass().getResource("/eeschema.sch").toURI());
        String contentType = Files.probeContentType(path);
        assertEquals("application/x-kicad-schematic", contentType);
    }
}
