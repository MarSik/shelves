package org.marsik.elshelves.backend.app.spring;

/*
 * EventBusPostProcessor.java
 * Author: Patrick Meade
 *
 * Updated to work with MBassador
 * Author: Martin Sivak
 *
 * EventBusPostProcessor.java is hereby placed into the public domain.
 * Use it as you see fit, and entirely at your own risk.
 */


import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;

/**
 * EventBusPostProcessor registers Spring beans with EventBus.
 * @author pmeade
 */
public class EventBusPostProcessor implements BeanPostProcessor
{
    private static final Logger log = LoggerFactory.getLogger(EventBusPostProcessor.class);
    @Autowired private MBassador<Object> eventBus;

    public static boolean containsSubscribeAnnotation(Object bean)
    {
        Method[] methods = bean.getClass().getMethods();
        for(Method method : methods)
        {
            Handler subscribe = method.getAnnotation(Handler.class);
            if(subscribe != null)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException
    {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException
    {
        if(containsSubscribeAnnotation(bean)) {
            eventBus.subscribe(bean);
            log.info("Bean {} containing @Handler annotation(s) was  "
                    + "registered with EventBus.", beanName);
        }
        return bean;
    }
}