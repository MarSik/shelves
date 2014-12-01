package org.marsik.elshelves.backend.configuration;

public class StorageConfiguration extends AbstractConfiguration {
    public String getDocumentPath() {
        String envPath = getDataPath();
        return envPath+"/documents";
    }
}
