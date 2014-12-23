package org.marsik.elshelves.backend.services;

import nl.marcus.ember.EmberSchema;
import nl.marcus.ember.EmberSchemaGenerator;
import org.marsik.elshelves.api.entities.BoxApiModel;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.api.entities.UserApiModel;
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
        emberSchemaGenerator.addHierarchy(UserApiModel.class);
        emberSchemaGenerator.addHierarchy(BoxApiModel.class);
        emberSchemaGenerator.addHierarchy(LotApiModel.class);
        emberSchemaGenerator.addHierarchy(PurchaseApiModel.class);

        emberSchema = emberSchemaGenerator.getEmberSchema();
    }

    public EmberSchema getEmberSchema() {
        return emberSchema;
    }
}
