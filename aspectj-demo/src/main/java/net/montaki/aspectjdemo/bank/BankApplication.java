package net.montaki.aspectjdemo.bank;

import net.montaki.aspectjdemo.bank.metier.Compte;
import net.montaki.aspectjdemo.bank.metier.IMetierBanque;
import net.montaki.aspectjdemo.bank.metier.MetierBanqueImpl;

public class BankApplication {
    private final ConsoleInput console;

    public BankApplication(ConsoleInput console) {
        this.console = console;
    }

    public static void main(String[] args) {
        ConsoleInput console = new ConsoleInput(System.in, System.out);
        try {
            new BankApplication(console).start();
        } catch (IllegalArgumentException | IllegalStateException | SecurityException exception) {
            console.print(exception.getMessage());
        }
    }

    public ConsoleInput getConsole() {
        return console;
    }

    public void start() {
        console.print("Demarrage de l'application bancaire");
        long code = Long.parseLong(console.read("Code du compte : "));
        double solde = Double.parseDouble(console.read("Solde initial : "));
        IMetierBanque banque = new MetierBanqueImpl();
        banque.addCompte(new Compte(code, solde));
        while (true) {
            String operation = console.read("Operation [V=versement, R=retrait, C=consulter, Q=quitter] : ");
            if ("Q".equalsIgnoreCase(operation) || "quitter".equalsIgnoreCase(operation)) {
                break;
            }
            try {
                switch (operation.toUpperCase(java.util.Locale.ROOT)) {
                    case "V" -> banque.verser(code, Double.parseDouble(console.read("Montant : ")));
                    case "R" -> banque.retirer(code, Double.parseDouble(console.read("Montant : ")));
                    case "C" -> { }
                    default -> throw new IllegalArgumentException("Operation inconnue");
                }
                console.print(banque.consulter(code).toString());
            } catch (IllegalArgumentException | IllegalStateException exception) {
                console.print(exception.getMessage());
            }
        }
        console.print("Fin de l'application bancaire");
    }
}
