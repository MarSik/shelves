package org.marsik.elshelves.backend.configuration;

public class DatabaseConfiguration extends AbstractConfiguration {
    public String getDbPath() {
        String envPath = getDataPath();
        return envPath+"/neo4j.db";
    }
}
