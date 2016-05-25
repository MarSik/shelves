package org.marsik.elshelves.backend.app.spring;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.FailureMode;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.spring.MemcachedClientFactoryBean;
import net.spy.memcached.transcoders.SerializingTranscoder;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.marsik.elshelves.backend.app.jackson.Jackson2CustomContextMapperBuilder;
import org.marsik.elshelves.backend.app.servlet.WebFormSupportFilter;
import org.marsik.elshelves.backend.app.mvc.RenamingProcessor;
import org.marsik.elshelves.backend.controllers.UserController;
import org.marsik.elshelves.backend.entities.converters.AbstractEmberToEntity;
import org.marsik.elshelves.backend.security.CurrentUserArgumentResolver;
import org.marsik.elshelves.backend.services.GoogleOauthService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessTokenJackson2Deserializer;
import org.springframework.security.oauth2.common.OAuth2AccessTokenJackson2Serializer;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.zalando.jackson.datatype.money.MoneyModule;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@EnableWebMvc
@ComponentScan(basePackageClasses = { UserController.class, AbstractEmberToEntity.class },
        excludeFilters = { @ComponentScan.Filter(classes = NoAutoscan.class) })
@Configuration
//@EnableAspectJAutoProxy
public class ApplicationRest extends WebMvcConfigurerAdapter {

    @Value("${http2.max-streams:10}")
    Integer http2MaxStreams;

    @Bean
    public EmbeddedServletContainerCustomizer customizer() {
        return new EmbeddedServletContainerCustomizer() {

            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
                if (container instanceof JettyEmbeddedServletContainerFactory) {
                    customizeJetty((JettyEmbeddedServletContainerFactory) container);
                }
            }

            private void customizeJetty(JettyEmbeddedServletContainerFactory jetty) {
                jetty.addServerCustomizers(new JettyServerCustomizer() {

                    @Override
                    public void customize(Server server) {
                        for (Connector connector : server.getConnectors()) {
                            if (connector instanceof ServerConnector) {
                                HttpConnectionFactory connectionFactory = ((ServerConnector) connector)
                                        .getConnectionFactory(HttpConnectionFactory.class);

                                HTTP2CServerConnectionFactory http2c = new HTTP2CServerConnectionFactory(connectionFactory.getHttpConfiguration());
                                http2c.setMaxConcurrentStreams(http2MaxStreams);

                                ((ServerConnector) connector).addConnectionFactory(http2c);
                            }
                        }
                    }
                });
            }
        };

    }

    private static class EtagHeaderFilter extends ShallowEtagHeaderFilter {
        @Override
        protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
            if (request.getRequestURI().endsWith(".stream")) {
                return true;
            }

            return super.shouldNotFilter(request);
        }
    }

    @Bean
    public Filter shallowEtagHeaderFilter() {
        return new EtagHeaderFilter();
    }

    @Bean
    public Filter getPutDeleteEmulator() {
        return new WebFormSupportFilter();
    }

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @Autowired
    CurrentUserArgumentResolver currentUserArgumentResolver;

    @Autowired
    RenamingProcessor renamingProcessor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        // Extra argument resolvers (the default ones are added as well).
        argumentResolvers.add(currentUserArgumentResolver);
        argumentResolvers.add(renamingProcessor);
    }

    @Bean
    MappingJackson2HttpMessageConverter emberJackson2HttpMessageConverter() {
        return getJacksonConverter();
    }

    public static MappingJackson2HttpMessageConverter getJacksonConverter() {
        Jackson2ObjectMapperBuilder builder = new Jackson2CustomContextMapperBuilder();
        builder.dateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")).indentOutput(true);

        builder.modulesToInstall(new JodaModule());
        builder.modulesToInstall(new MoneyModule());

        builder.serializerByType(OAuth2AccessToken.class, new OAuth2AccessTokenJackson2Serializer());
        builder.deserializerByType(OAuth2AccessToken.class, new OAuth2AccessTokenJackson2Deserializer());
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(builder.build());
        return converter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        converters.add(new StringHttpMessageConverter());
        converters.add(emberJackson2HttpMessageConverter());
    }

    @Bean
    public ModelMapper configureModelMapper() {
        final ModelMapper mm = new ModelMapper();
        return mm;
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!registry.hasMappingForPattern("/webjars/**")) {
            registry.addResourceHandler("/webjars/**").addResourceLocations(
                    "classpath:/META-INF/resources/webjars/");
        }

        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("/favicon.ico");
    }
}
