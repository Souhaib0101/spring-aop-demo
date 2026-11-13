package net.montaki.aspectjdemo.bank.metier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MetierBanqueImpl implements IMetierBanque {
    private final Map<Long, Compte> comptes = new HashMap<>();

    @Override
    public void addCompte(Compte compte) {
        Objects.requireNonNull(compte, "Le compte est obligatoire");
        if (comptes.putIfAbsent(compte.getCode(), compte) != null) {
            throw new IllegalArgumentException("Compte deja existant : " + compte.getCode());
        }
    }

    @Override
    public Compte consulter(long code) {
        Compte compte = comptes.get(code);
        if (compte == null) {
            throw new IllegalArgumentException("Compte introuvable : " + code);
        }
        return compte;
    }

    @Override
    public void verser(long code, double montant) {
        verifierMontant(montant);
        Compte compte = consulter(code);
        double solde = compte.getSolde() + montant;
        if (!Double.isFinite(solde)) {
            throw new IllegalArgumentException("Le solde depasse la capacite de calcul");
        }
        compte.setSolde(solde);
    }

    @Override
    public void retirer(long code, double montant) {
        verifierMontant(montant);
        Compte compte = consulter(code);
        // Omission volontaire du controle du solde : corrigee par PatchRetraitAspect.
        compte.setSolde(compte.getSolde() - montant);
    }

    private void verifierMontant(double montant) {
        if (!Double.isFinite(montant) || montant <= 0) {
            throw new IllegalArgumentException("Le montant doit etre strictement positif et fini");
        }
    }
}
