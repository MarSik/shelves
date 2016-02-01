package org.marsik.elshelves.backend.test.converters;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.marsik.elshelves.backend.services.UuidGenerator;
import org.marsik.elshelves.backend.services.UuidGeneratorImpl;
import org.marsik.elshelves.backend.test.config.ConverterOnlyApplication;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringApplicationConfiguration(classes = { ConverterOnlyApplication.class })
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"test", "unittest"})
public abstract class BaseConverterTest {
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Bean
    public UuidGenerator uuidGenerator() {
        return new UuidGeneratorImpl();
    }
}
