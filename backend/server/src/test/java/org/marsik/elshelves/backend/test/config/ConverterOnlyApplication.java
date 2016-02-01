package org.marsik.elshelves.backend.test.config;

import org.marsik.elshelves.backend.app.spring.NoAutoscan;
import org.marsik.elshelves.backend.entities.converters.AbstractEmberToEntity;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackageClasses = { AbstractEmberToEntity.class },
        excludeFilters = { @ComponentScan.Filter(classes = NoAutoscan.class) })
@Configuration
@NoAutoscan
public class ConverterOnlyApplication {
}
