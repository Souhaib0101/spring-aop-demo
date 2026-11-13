package net.montaki.aspectjdemo.aspects;

import net.montaki.aspectjdemo.bank.metier.IMetierBanque;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class PatchRetraitAspect {
    @Around(value = "execution(void net.montaki.aspectjdemo.bank.metier.MetierBanqueImpl.retirer(long, double))"
            + " && args(code,montant)", argNames = "joinPoint,code,montant")
    public Object verifierSolde(ProceedingJoinPoint joinPoint, long code, double montant) throws Throwable {
        IMetierBanque banque = (IMetierBanque) joinPoint.getTarget();
        if (banque.consulter(code).getSolde() < montant) {
            throw new IllegalStateException("Solde insuffisant");
        }
        return joinPoint.proceed();
    }
}
