package net.montaki.aspectjdemo.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class SecondAspect {
    @Pointcut("execution(public static void net.montaki.aspectjdemo.basics.Application.main(String[]))")
    public void mainExecution() {
    }

    @Around("mainExecution()")
    public Object aroundMain(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("Before main - syntaxe annotations");
        try {
            return joinPoint.proceed();
        } finally {
            System.out.println("After main - syntaxe annotations");
        }
    }
}
