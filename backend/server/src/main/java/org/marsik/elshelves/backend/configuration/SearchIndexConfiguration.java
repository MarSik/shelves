package org.marsik.elshelves.backend.configuration;

public class SearchIndexConfiguration extends AbstractConfiguration {
    public String getSearchIndexPath() {
        String envPath = getDataPath();
        return envPath+"/search.db";
    }
}
