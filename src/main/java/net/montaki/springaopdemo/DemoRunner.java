package net.montaki.springaopdemo;

import net.montaki.springaopdemo.security.SecurityContext;
import net.montaki.springaopdemo.service.IMetier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "demo.enabled", havingValue = "true", matchIfMissing = true)
public class DemoRunner implements CommandLineRunner {
    private final IMetier metier;
    private final String username;
    private final String password;

    public DemoRunner(IMetier metier, @Value("${demo.user:root}") String username,
                      @Value("${demo.password:1234}") String password) {
        this.metier = metier;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        System.out.println("Proxy utilise : " + metier.getClass().getName());
        try {
            SecurityContext.authenticate(username, password);
            System.out.println("Utilisateur : " + SecurityContext.getUsername());
            invoke(metier::process);
            invoke(() -> System.out.println("Resultat : " + metier.compute()));
        } catch (SecurityException exception) {
            System.out.println(exception.getMessage());
        } finally {
            SecurityContext.clear();
        }
    }

    private void invoke(Runnable operation) {
        try {
            operation.run();
        } catch (SecurityException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
