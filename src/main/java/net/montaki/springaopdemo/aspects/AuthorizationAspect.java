package net.montaki.springaopdemo.aspects;

import java.util.Arrays;

import net.montaki.springaopdemo.security.SecurityContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class AuthorizationAspect {
    @Around(value = "@annotation(securedByAspect)", argNames = "joinPoint,securedByAspect")
    public Object secure(ProceedingJoinPoint joinPoint, SecuredByAspect securedByAspect) throws Throwable {
        if (!SecurityContext.isAuthenticated()) {
            throw new SecurityException("Authentification requise pour " + joinPoint.getSignature().toShortString());
        }
        boolean authorized = Arrays.stream(securedByAspect.roles()).anyMatch(SecurityContext::hasRole);
        if (!authorized) {
            throw new SecurityException("Acces refuse a " + joinPoint.getSignature().toShortString()
                    + " pour " + SecurityContext.getUsername());
        }
        return joinPoint.proceed();
    }
}
