package org.marsik.elshelves.backend.app.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.strategy.HystrixPlugins;
import gnu.trove.map.hash.THashMap;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

@Aspect
@Component
public class CircuitBreakerAspect {
    private static Map<String, String> names = new THashMap<>();
    private static Map<String, String> groups = new THashMap<>();

    @Pointcut("within(@org.marsik.elshelves.backend.app.hystrix.CircuitBreaker *)")
    public void circuitBreakerAnnotationPresent() {}

    @Pointcut("within(@org.springframework.web.bind.annotation.RequestMapping *)")
    public void requestMappingPresent() {}

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {}

    @Pointcut("execution(* org.marsik.elshelves.backend.controllers.*.*(..)) && publicMethod() && requestMappingPresent()")
    public void controllerMethod() {}

    @Pointcut("execution(* org.marsik.elshelves.backend.repositories.*.*(..)) && publicMethod()")
    public void repositoryMethod() {}

    @Pointcut("execution(* org.marsik.elshelves.backend.services.*.*(..)) && publicMethod()")
    public void serviceMethod() {}

    @Around("circuitBreakerAnnotationPresent() || repositoryMethod() || controllerMethod()")
    public Object circuitBreakerAround(final ProceedingJoinPoint aJoinPoint) throws Throwable {
        CircuitBreaker annotation = null;

        String theGroupName = names.get(aJoinPoint.toLongString());
        String theCmdName = groups.get(aJoinPoint.toLongString());

        if (theCmdName == null || theGroupName == null) {
            if (aJoinPoint.getSignature() instanceof MethodSignature) {
                Class tgt = aJoinPoint.getTarget().getClass();
                while (tgt != null && annotation == null) {
                    try {
                        Method m = tgt.getDeclaredMethod(aJoinPoint.getSignature().getName(),
                                ((MethodSignature) aJoinPoint.getSignature()).getParameterTypes());
                        annotation = m.getDeclaredAnnotation(CircuitBreaker.class);
                        break;
                    } catch (NoSuchMethodException ex) {
                        tgt = tgt.getSuperclass();
                    }
                }
            }

            if (annotation != null) {
                if (!annotation.group().isEmpty()) {
                    theGroupName = annotation.group();
                }

                if (!annotation.value().isEmpty()) {
                    theCmdName = annotation.value();

                    if (theGroupName == null) {
                        theGroupName = theCmdName;
                    }
                }
            }

            String theRealName = aJoinPoint.getSignature().toShortString();
            if (theGroupName == null) {
                theGroupName = aJoinPoint.getTarget().getClass().getSimpleName();
            }

            if (theCmdName == null) {
                theCmdName = theRealName.startsWith(theGroupName) ? theRealName : theGroupName + "::" + theRealName;
            }

            names.put(aJoinPoint.toLongString(), theCmdName);
            groups.put(aJoinPoint.toLongString(), theGroupName);
        }

        HystrixCommand.Setter theSetter =
                HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(theGroupName));
        theSetter = theSetter.andCommandKey(HystrixCommandKey.Factory.asKey(theCmdName));
        theSetter = theSetter.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE));

        HystrixCommand hystrixCommand = new HystrixCommand(theSetter) {
            @Override
            protected Object run() throws Exception {
                try {
                    return aJoinPoint.proceed();
                } catch (Exception e) {
                    throw e;
                } catch (Throwable e) {
                    throw new Exception(e);
                }
            }
        };

        return hystrixCommand.execute();
    }
}
