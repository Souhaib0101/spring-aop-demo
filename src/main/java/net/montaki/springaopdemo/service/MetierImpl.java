package net.montaki.springaopdemo.service;

import net.montaki.springaopdemo.aspects.Log;
import net.montaki.springaopdemo.aspects.SecuredByAspect;
import org.springframework.stereotype.Service;

@Service
public class MetierImpl implements IMetier {
    @Override
    @Log
    @SecuredByAspect(roles = {"USER", "ADMIN"})
    public void process() {
        System.out.println("Business Process");
    }

    @Override
    @Log
    @SecuredByAspect(roles = {"ADMIN"})
    public double compute() {
        System.out.println("Business Computing");
        return 21.0 * 2;
    }
}
