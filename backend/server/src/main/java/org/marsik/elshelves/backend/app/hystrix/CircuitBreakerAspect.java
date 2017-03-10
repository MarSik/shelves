package org.marsik.elshelves.backend.app.hystrix;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import gnu.trove.map.hash.THashMap;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import rx.Observable;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

@Aspect
@Component
public class CircuitBreakerAspect {
    public static final int DEFAULT_TIMEOUT = 10000;

    private static Map<String, String> names = new THashMap<>();
    private static Map<String, String> groups = new THashMap<>();

    @Pointcut("@annotation(org.marsik.elshelves.backend.app.hystrix.CircuitBreaker)")
    public void circuitBreakerAnnotationPresent() {}

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMappingPresent() {}

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {}

    @Pointcut("execution(* org.marsik.elshelves.backend.controllers.*.*(..)) && publicMethod() && requestMappingPresent()")
    public void controllerMethod() {}

    @Pointcut("execution(* org.marsik.elshelves.backend.repositories.*.*(..)) && publicMethod()")
    public void repositoryMethod() {}

    @Pointcut("execution(* org.marsik.elshelves.backend.services.*.*(..)) && publicMethod()")
    public void serviceMethod() {}

    @Around("circuitBreakerAnnotationPresent() || serviceMethod() || repositoryMethod() || controllerMethod()")
    public Object circuitBreakerAround(final ProceedingJoinPoint aJoinPoint) throws Throwable {
        CircuitBreaker annotation = null;

        String theGroupName = groups.get(aJoinPoint.toLongString());
        String theCmdName = names.get(aJoinPoint.toLongString());

        if (theCmdName == null || theGroupName == null) {
            final Object target = aJoinPoint.getTarget();
            final Class<?> aClass = target.getClass();
            if (aJoinPoint.getSignature() instanceof MethodSignature) {
                Class tgt = aClass;
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
                if (target instanceof Proxy
                        || target instanceof org.springframework.cglib.proxy.Proxy) {
                    theGroupName = aClass.getInterfaces()[0].getSimpleName();
                } else {
                    theGroupName = aClass.getSimpleName();
                }
            }

            if (theCmdName == null) {
                theCmdName = theRealName.startsWith(theGroupName) ? theRealName : theGroupName + "::" + theRealName;
            }

            names.put(aJoinPoint.toLongString(), theCmdName);
            groups.put(aJoinPoint.toLongString(), theGroupName);
        }



        HystrixObservableCommand.Setter theSetter =
                HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(theGroupName));
        theSetter = theSetter.andCommandKey(HystrixCommandKey.Factory.asKey(theCmdName));
        theSetter = theSetter.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                .withExecutionTimeoutInMilliseconds(annotation != null && annotation.timeoutMs() != 0
                        ? annotation.timeoutMs() : DEFAULT_TIMEOUT));

        HystrixObservableCommand<Object> hystrixCommand = new HystrixObservableCommand<Object>(theSetter) {
            @Override
            protected Observable<Object> construct() {
                return Observable.create(subscriber -> {
                    try {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(aJoinPoint.proceed());
                            subscriber.onCompleted();
                        }

                    } catch (Throwable throwable) {
                        subscriber.onError(throwable);
                    }
                });
            }
        };

        return hystrixCommand.toObservable().toBlocking().toFuture().get();
    }
}
