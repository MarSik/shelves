package org.marsik.elshelves.backend.configuration;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class MailgunConfiguration extends AbstractConfiguration {
    static final Logger logger = LoggerFactory.getLogger(MailgunConfiguration.class);
    public String getKey() {
        try {
            String path = getBasePath() + "/mailgun";
            File f = new File(path);
            return FileUtils.readFileToString(f).trim();
        } catch (IOException ex) {
            logger.error("Failed to get Mailgun configuration", ex);
            return "";
        }
    }
}
