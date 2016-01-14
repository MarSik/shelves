package org.marsik.elshelves.backend.test.unit;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.marsik.elshelves.backend.test.config.EmptyApplication;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringApplicationConfiguration(classes = { EmptyApplication.class })
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"test", "unittest"})
public abstract class BaseUnitTest {
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
}
