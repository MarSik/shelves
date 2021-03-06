package org.marsik.elshelves.backend.test.integration;

import org.junit.Test;
import org.marsik.elshelves.backend.controllers.DocumentController;
import org.marsik.elshelves.backend.entities.converters.DocumentToEmber;
import org.marsik.elshelves.backend.entities.converters.EmberToDocument;
import org.marsik.elshelves.backend.services.DocumentService;
import org.marsik.elshelves.backend.services.StorageManager;
import org.marsik.elshelves.backend.test.unit.BaseUnitTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class DocumentControllerTest extends BaseIntegrationTest {
    @InjectMocks
    DocumentController controller;

    @Mock
    StorageManager storageManager;

    @Mock
    DocumentService documentService;

    @Mock
    DocumentToEmber documentToEmber;

    @Mock
    EmberToDocument emberToDocument;

    @Test
    public void testSimpleCreation() throws Exception {

    }
}
