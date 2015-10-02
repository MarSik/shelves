package org.marsik.elshelves.backend.app.spring;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import rx.Observable;

@Aspect
@Component
public class CircuitBreakerAspect {
    @Around("@annotation(org.marsik.elshelves.backend.app.spring.CircuitBreaker)")
    public Object circuitBreakerAround(final ProceedingJoinPoint aJoinPoint) throws Throwable {
        String theGroupName = aJoinPoint.getSignature().toShortString();
        String theCmdName = aJoinPoint.toShortString();

        HystrixCommand.Setter theSetter =
                HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(theGroupName));
        theSetter = theSetter.andCommandKey(HystrixCommandKey.Factory.asKey(theCmdName));

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
