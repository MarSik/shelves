package org.marsik.elshelves.backend.test.unit;

import org.junit.Test;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.marsik.elshelves.backend.services.BoxService;
import org.marsik.elshelves.backend.services.BoxServiceImpl;
import org.marsik.elshelves.backend.services.UuidGenerator;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class BoxServiceUnitTest extends BaseUnitTest {
    @InjectMocks
    BoxServiceImpl service;

    @Mock
    BoxRepository repository;

    @Mock
    UuidGenerator uuidGenerator;

    @Test
    public void testTest() throws Exception {

    }
}
