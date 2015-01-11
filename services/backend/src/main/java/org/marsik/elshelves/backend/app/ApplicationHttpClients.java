package org.marsik.elshelves.backend.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationHttpClients {
	@Bean
	HttpComponentsClientHttpRequestFactory getHttpClientRequestFactory() {
		return new HttpComponentsClientHttpRequestFactory();
	}

	@Bean
	RestTemplate getRestTemplate() {
		return new RestTemplate(getHttpClientRequestFactory());
	}
}
