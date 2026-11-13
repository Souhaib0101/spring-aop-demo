package net.montaki.springaopdemo.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(2)
public class LogAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);

    @Around("@annotation(net.montaki.springaopdemo.aspects.Log)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        long start = System.nanoTime();
        LOGGER.info("Avant {}", method);
        try {
            return joinPoint.proceed();
        } catch (Throwable exception) {
            LOGGER.warn("Echec {} : {}", method, exception.getMessage());
            throw exception;
        } finally {
            LOGGER.info("Apres {} - duree={} ms", method, (System.nanoTime() - start) / 1_000_000.0);
        }
    }
}
