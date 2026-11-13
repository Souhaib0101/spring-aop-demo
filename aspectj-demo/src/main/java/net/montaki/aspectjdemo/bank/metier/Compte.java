package net.montaki.aspectjdemo.bank.metier;

public class Compte {
    private final long code;
    private double solde;

    public Compte(long code, double solde) {
        if (!Double.isFinite(solde) || solde < 0) {
            throw new IllegalArgumentException("Le solde initial doit etre positif ou nul et fini");
        }
        this.code = code;
        this.solde = solde;
    }

    public long getCode() {
        return code;
    }

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    @Override
    public String toString() {
        return "Compte{code=" + code + ", solde=" + solde + '}';
    }
}
