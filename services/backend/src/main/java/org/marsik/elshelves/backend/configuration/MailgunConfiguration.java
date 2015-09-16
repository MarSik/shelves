package org.marsik.elshelves.backend.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MailgunConfiguration extends AbstractConfiguration {
    static final Logger logger = LoggerFactory.getLogger(MailgunConfiguration.class);

	final Properties properties = new Properties();

	public void load() {
		String path = getBasePath() + "/mailgun.properties";
		try (FileInputStream f = new FileInputStream(path)) {
			properties.load(f);
		} catch (IOException ex) {
			logger.error("Failed to get Mailgun configuration", ex);
		}
	}

	public String getKey() {
		return properties.getProperty("key", "");
    }

	public String getUrl() {
		return properties.getProperty("url", "https://api.mailgun.net/v2/shelves.cz/messages");
	}
}
