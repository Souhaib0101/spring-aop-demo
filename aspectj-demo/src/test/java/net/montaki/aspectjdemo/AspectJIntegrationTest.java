package net.montaki.aspectjdemo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import net.montaki.aspectjdemo.bank.BankApplication;
import net.montaki.aspectjdemo.bank.ConsoleInput;
import net.montaki.aspectjdemo.bank.metier.Compte;
import net.montaki.aspectjdemo.bank.metier.IMetierBanque;
import net.montaki.aspectjdemo.bank.metier.MetierBanqueImpl;
import net.montaki.aspectjdemo.basics.Application;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AspectJIntegrationTest {
    @Test
    void bothAspectSyntaxesWrapMainInTheDeclaredOrder() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try (PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            System.setOut(output);
            Application.main(new String[0]);
        } finally {
            System.setOut(original);
        }
        assertEquals("""
                Before main - syntaxe annotations
                Before main - syntaxe AspectJ
                Message from main application
                After main - syntaxe AspectJ
                After main - syntaxe annotations
                """, bytes.toString(StandardCharsets.UTF_8).replace("\r\n", "\n"));
    }

    @Test
    void depositsAndWithdrawalsKeepTheExpectedBalance() {
        IMetierBanque banque = banque();
        banque.verser(1, 50);
        banque.retirer(1, 30);
        assertEquals(120, banque.consulter(1).getSolde());
        banque.retirer(1, 120);
        assertEquals(0, banque.consulter(1).getSolde());
    }

    @Test
    void wovenPatchBlocksOverdraftEvenOnAnObjectCreatedWithNew() {
        IMetierBanque banque = banque();
        IllegalStateException error = assertThrows(IllegalStateException.class, () -> banque.retirer(1, 101));
        assertEquals("Solde insuffisant", error.getMessage());
        assertEquals(100, banque.consulter(1).getSolde());
    }

    @Test
    void invalidAmountsNeverModifyTheAccount() {
        IMetierBanque banque = banque();
        for (double montant : new double[]{0, -10, Double.NaN, Double.POSITIVE_INFINITY}) {
            assertThrows(RuntimeException.class, () -> banque.verser(1, montant));
            assertThrows(RuntimeException.class, () -> banque.retirer(1, montant));
        }
        assertEquals(100, banque.consulter(1).getSolde());
    }

    @Test
    void unknownAccountsAndDuplicatesAreReported() {
        IMetierBanque banque = banque();
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> banque.consulter(99)),
                () -> assertThrows(IllegalArgumentException.class, () -> banque.verser(99, 10)),
                () -> assertThrows(IllegalArgumentException.class, () -> banque.retirer(99, 10)),
                () -> assertThrows(IllegalArgumentException.class, () -> banque.addCompte(new Compte(1, 50)))
        );
        assertEquals(100, banque.consulter(1).getSolde());
    }

    @Test
    void authenticationRunsBeforeTheApplicationStarts() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BankApplication application = application("inconnu\nincorrect\n", bytes);
        assertThrows(SecurityException.class, application::start);
        assertFalse(bytes.toString(StandardCharsets.UTF_8).contains("Demarrage"));
    }

    @Test
    void consoleSessionRecoversFromAnOverdraftAndQuits() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        application("root\n1234\n1\n100\nV\n50\nR\n200\nR\n20\nQ\n", bytes).start();
        String output = bytes.toString(StandardCharsets.UTF_8);
        assertAll(
                () -> assertTrue(output.contains("Solde insuffisant")),
                () -> assertTrue(output.contains("solde=130.0")),
                () -> assertTrue(output.contains("Fin de l'application bancaire"))
        );
    }

    @Test
    void logContainsInitializationNestedCallsFailuresAndTimings() throws Exception {
        IMetierBanque banque = banque();
        assertThrows(IllegalStateException.class, () -> banque.retirer(1, 200));
        String log = Files.readString(Path.of(System.getProperty("demo.bank.log")));
        assertAll(
                () -> assertTrue(log.contains("Avant initialisation")),
                () -> assertTrue(log.contains("Apres initialisation")),
                () -> assertTrue(log.contains("Avant MetierBanqueImpl.consulter")),
                () -> assertTrue(log.contains("Echec MetierBanqueImpl.retirer")),
                () -> assertTrue(log.contains("duree="))
        );
    }

    private IMetierBanque banque() {
        IMetierBanque banque = new MetierBanqueImpl();
        banque.addCompte(new Compte(1, 100));
        return banque;
    }

    private BankApplication application(String input, ByteArrayOutputStream output) {
        return new BankApplication(new ConsoleInput(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output, true, StandardCharsets.UTF_8)));
    }
}
