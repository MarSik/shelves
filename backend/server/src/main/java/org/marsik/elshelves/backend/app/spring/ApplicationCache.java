package org.marsik.elshelves.backend.app.spring;

import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.FailureMode;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.spring.MemcachedClientFactoryBean;
import net.spy.memcached.transcoders.SerializingTranscoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationCache {
    @Value("${shelves.memcached.server:127.0.0.1}")
    String memcachedServer;

    @Value("${shelves.memcached.port:11211}")
    String memcachedPort;

    @Bean
    MemcachedClient getMemcachedClientFactory() {
        MemcachedClientFactoryBean memcached = new MemcachedClientFactoryBean();
        memcached.setServers(memcachedServer+":"+memcachedPort);
        memcached.setProtocol(ConnectionFactoryBuilder.Protocol.BINARY);
        SerializingTranscoder transcoder = new SerializingTranscoder();
        transcoder.setCompressionThreshold(1024);
        memcached.setTranscoder(transcoder);
        memcached.setOpTimeout(1000);
        memcached.setTimeoutExceptionThreshold(1998);
        memcached.setLocatorType(ConnectionFactoryBuilder.Locator.CONSISTENT);
        memcached.setFailureMode(FailureMode.Redistribute);
        memcached.setUseNagleAlgorithm(false);

        try {
            return (MemcachedClient) (memcached.getObject());
        } catch (Exception e) {
            return null;
        }
    }
}
