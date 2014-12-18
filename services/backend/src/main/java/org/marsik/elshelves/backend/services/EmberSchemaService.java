package org.marsik.elshelves.backend.services;

import com.google.common.reflect.ClassPath;
import nl.marcus.ember.EmberSchema;
import nl.marcus.ember.EmberSchemaGenerator;
import org.marsik.elshelves.api.entities.Box;
import org.marsik.elshelves.api.entities.Lot;
import org.marsik.elshelves.api.entities.Purchase;
import org.marsik.elshelves.api.entities.User;
import org.marsik.elshelves.backend.app.spring.EmberAwareObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmberSchemaService {
    final EmberAwareObjectMapper emberAwareObjectMapper;
    final EmberSchemaGenerator emberSchemaGenerator;
    final EmberSchema emberSchema;

    @Autowired
    public EmberSchemaService(EmberAwareObjectMapper emberAwareObjectMapper) {
        this.emberAwareObjectMapper = emberAwareObjectMapper;
        emberSchemaGenerator = new EmberSchemaGenerator(emberAwareObjectMapper);
/*
        XXX: The getLinks need to be ignored by the scanner first to make this work*/
        emberSchemaGenerator.addHierarchy(User.class);
        emberSchemaGenerator.addHierarchy(Box.class);
        emberSchemaGenerator.addHierarchy(Lot.class);
        emberSchemaGenerator.addHierarchy(Purchase.class);

        emberSchema = emberSchemaGenerator.getEmberSchema();
    }

    public EmberSchema getEmberSchema() {
        return emberSchema;
    }
}
