package net.montaki.aspectjdemo.aspects;

import net.montaki.aspectjdemo.bank.BankApplication;
import net.montaki.aspectjdemo.bank.ConsoleInput;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class SecurityAspect {
    @Around("execution(void net.montaki.aspectjdemo.bank.BankApplication.start())")
    public Object authenticate(ProceedingJoinPoint joinPoint) throws Throwable {
        ConsoleInput console = ((BankApplication) joinPoint.getTarget()).getConsole();
        String username = console.read("Username : ");
        String password = console.read("Password : ");
        if (!"root".equals(username) || !"1234".equals(password)) {
            throw new SecurityException("Access denied");
        }
        return joinPoint.proceed();
    }
}
