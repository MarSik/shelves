package org.marsik.elshelves.backend.entities;

import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

import java.util.UUID;

@NodeEntity
public class User extends org.marsik.elshelves.api.entities.User {
    @Indexed(unique = true)
    UUID uuid;


}
