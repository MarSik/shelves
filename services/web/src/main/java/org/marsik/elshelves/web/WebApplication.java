package org.marsik.elshelves.web;

import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.FailureMode;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.spring.MemcachedClientFactoryBean;
import net.spy.memcached.transcoders.SerializingTranscoder;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import javax.servlet.Filter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@EnableAutoConfiguration
@ComponentScan("org.marsik.elshelves.web")
@EnableWebMvc
@Configuration
public class WebApplication extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    public class RandomStringMethod implements TemplateMethodModelEx {
        public TemplateModel exec(List args) throws TemplateModelException {
            if (args.size() != 1) {
                throw new TemplateModelException("Wrong arguments");
            }
            return new SimpleScalar(RandomStringUtils.randomAlphanumeric(((SimpleNumber)args.get(0)).getAsNumber().intValue()));
        }
    }

    @Bean
    @Autowired
    public ViewResolver viewResolver(MemcachedClient memcache) {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        resolver.setCache(false);
        resolver.setPrefix("");
        resolver.setSuffix(".ftl");
        resolver.setContentType("text/html;charset=utf-8");

        Map<String, Object> globals = new HashMap<String, Object>();
        globals.put("randomString", new RandomStringMethod());
        globals.put("cache", new MemcachedBlock(memcache));

        resolver.setAttributesMap(globals);
        return resolver;
    }

    @Bean
    MemcachedClient getMemcachedClientFactory() {
        MemcachedClientFactoryBean memcached = new MemcachedClientFactoryBean();
        memcached.setServers("127.0.0.1:11211");
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

    @Bean
    public FreeMarkerConfigurer freemarkerConfig() throws IOException, TemplateException {
        FreeMarkerConfigurer result = new FreeMarkerConfigurer();
        result.setTemplateLoaderPath("WEB-INF/freemarker");
        result.setDefaultEncoding("UTF-8");
        return result;
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registry.addResourceHandler("/index.html")
                .addResourceLocations("/");
        registry.addResourceHandler("/robots.txt")
                .addResourceLocations("/");
        registry.addResourceHandler("/crossdomain.xml")
                .addResourceLocations("/");
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("/assets/");
    }

    @Bean(name = "messageSource")
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("WEB-INF/locale/messages");
        messageSource.setCacheSeconds(1);
        messageSource.setFallbackToSystemLocale(true);
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor(){
        LocaleChangeInterceptor localeChangeInterceptor=new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    @Bean(name = "localeResolver")
    public LocaleResolver localeResolver(){
        SessionLocaleResolver localeResolver=new SessionLocaleResolver();
        localeResolver.setDefaultLocale(new Locale("cs","CZ"));
        return localeResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(localeChangeInterceptor());
    }
}
