package org.marsik.elshelves.backend.test.unit;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.marsik.elshelves.ApplicationLauncher;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringApplicationConfiguration(classes = { EmptyApplication.class })
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class BaseUnitTest {
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
}
