package net.montaki.aspectjdemo.bank;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/** Un seul scanner partage entre l'aspect d'authentification et l'application. */
public class ConsoleInput {
    private final Scanner scanner;
    private final PrintStream output;

    public ConsoleInput(InputStream input, PrintStream output) {
        this.scanner = new Scanner(input);
        this.output = output;
    }

    public String read(String prompt) {
        output.print(prompt);
        if (!scanner.hasNextLine()) {
            throw new IllegalStateException("Fin de la saisie");
        }
        return scanner.nextLine().trim();
    }

    public void print(String message) {
        output.println(message);
    }
}
