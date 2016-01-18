package org.marsik.elshelves.backend.test.dao;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.marsik.elshelves.backend.test.config.DaoApplication;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringApplicationConfiguration(classes = { DaoApplication.class })
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"test", "dao"})
public abstract class BaseDaoTest {
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
}
