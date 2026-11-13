package net.montaki.aspectjdemo.aspects;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.XMLFormatter;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class LoggingAspect {
    private static final Logger LOGGER = createLogger();

    @Pointcut("execution(public * net.montaki.aspectjdemo.bank.metier.MetierBanqueImpl.*(..))")
    public void businessMethods() {
    }

    @Around("businessMethods()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        String signature = joinPoint.getSignature().toShortString();
        long start = System.nanoTime();
        LOGGER.info("Avant " + signature);
        try {
            return joinPoint.proceed();
        } catch (Throwable exception) {
            LOGGER.log(Level.WARNING, "Echec " + signature + " : " + exception.getMessage());
            throw exception;
        } finally {
            LOGGER.info("Apres " + signature + " - duree=" + (System.nanoTime() - start) / 1_000_000.0 + " ms");
        }
    }

    @Before("initialization(net.montaki.aspectjdemo.bank.metier.MetierBanqueImpl.new(..))")
    public void beforeInitialization(JoinPoint joinPoint) {
        LOGGER.info("Avant initialisation " + joinPoint.getSignature().toShortString());
    }

    @After("initialization(net.montaki.aspectjdemo.bank.metier.MetierBanqueImpl.new(..))")
    public void afterInitialization(JoinPoint joinPoint) {
        LOGGER.info("Apres initialisation " + joinPoint.getSignature().toShortString());
    }

    private static Logger createLogger() {
        Logger logger = Logger.getLogger(LoggingAspect.class.getName());
        logger.setUseParentHandlers(false);
        try {
            Path path = Path.of(System.getProperty("demo.bank.log", "logs/bank.xml")).toAbsolutePath();
            Files.createDirectories(path.getParent());
            FileHandler handler = new FileHandler(path.toString(), false);
            handler.setEncoding("UTF-8");
            handler.setFormatter(new XMLFormatter());
            logger.addHandler(handler);
            Runtime.getRuntime().addShutdownHook(new Thread(handler::close));
        } catch (IOException exception) {
            throw new IllegalStateException("Impossible de creer le journal bancaire", exception);
        }
        return logger;
    }
}
