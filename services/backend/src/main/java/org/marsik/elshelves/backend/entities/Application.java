package org.marsik.elshelves.backend.entities;

import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class Application extends org.marsik.elshelves.api.entities.Application {
    /**
     * This is used for authentization to the Oauth2 endpoint
     */
    String apiKey;
}
