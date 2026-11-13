package net.montaki.aspectjdemo.bank.metier;

public interface IMetierBanque {
    void addCompte(Compte compte);

    Compte consulter(long code);

    void verser(long code, double montant);

    void retirer(long code, double montant);
}
