package org.marsik.elshelves.backend.configuration;

public abstract class AbstractConfiguration {
    public String getPackageRoot() {
        return "org.marsik.elshelves.services.backend";
    }

    public String getBasePath() {
        String envPath = System.getenv("SHV_CONFIG");
        if (envPath == null) {
            envPath = System.getProperty(getPackageRoot() + ".config");
        }
        if (envPath == null) {
            envPath = "/etc/elshelves/backend";
        }
        return envPath;
    }

    public String getStaticDataPath() {
        String envPath = System.getenv("SHV_STATIC");
        if (envPath == null) {
            envPath = System.getProperty(getPackageRoot() + ".static");
        }
        if (envPath == null) {
            envPath = "/usr/share/elshelves/backend";
        }
        return envPath;
    }

    public String getDataPath() {
        String envPath = System.getenv("SHV_DATA");
        if (envPath == null) {
            envPath = System.getProperty(getPackageRoot() + ".data");
        }
        if (envPath == null) {
            envPath = "/var/lib/elshelves/backend";
        }
        return envPath;
    }
}
