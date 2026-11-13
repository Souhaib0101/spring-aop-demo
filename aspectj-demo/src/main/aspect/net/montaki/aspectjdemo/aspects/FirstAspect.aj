package net.montaki.aspectjdemo.aspects;

public aspect FirstAspect {
    pointcut mainExecution():
        execution(public static void net.montaki.aspectjdemo.basics.Application.main(String[]));

    before(): mainExecution() {
        System.out.println("Before main - syntaxe AspectJ");
    }

    after(): mainExecution() {
        System.out.println("After main - syntaxe AspectJ");
    }
}
