package net.montaki.aspectjdemo.aspects;

public aspect AspectPrecedence {
    declare precedence: SecondAspect, FirstAspect, SecurityAspect, LoggingAspect, PatchRetraitAspect, *;
}
