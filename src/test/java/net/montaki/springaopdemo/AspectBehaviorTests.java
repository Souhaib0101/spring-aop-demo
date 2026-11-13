package net.montaki.springaopdemo;

import net.montaki.springaopdemo.aspects.AuthorizationAspect;
import net.montaki.springaopdemo.aspects.Log;
import net.montaki.springaopdemo.aspects.LogAspect;
import net.montaki.springaopdemo.security.SecurityContext;
import net.montaki.springaopdemo.service.IMetier;
import net.montaki.springaopdemo.service.MetierImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(OutputCaptureExtension.class)
class AspectBehaviorTests {
    @AfterEach
    void clearSecurityContext() {
        SecurityContext.clear();
    }

    @Test
    void sameAnnotationsWorkWithACglibProxy() {
        new ApplicationContextRunner().withUserConfiguration(CglibConfiguration.class).run(context -> {
            IMetier metier = context.getBean(IMetier.class);
            assertThat(AopUtils.isCglibProxy(metier)).isTrue();
            assertThatThrownBy(metier::compute).isInstanceOf(SecurityException.class);
            SecurityContext.authenticate("root", "1234");
            assertThat(metier.compute()).isEqualTo(42.0);
        });
    }

    @Test
    void loggingIsSelectiveAndPreservesExceptions(CapturedOutput output) {
        AspectJProxyFactory factory = new AspectJProxyFactory(new SampleService());
        factory.addAspect(new LogAspect());
        SampleService service = factory.getProxy();
        assertThat(service.unmarked()).isEqualTo("sans journalisation");
        assertThat(output).doesNotContain("unmarked");
        assertThatThrownBy(service::fail).isSameAs(SampleService.FAILURE);
        assertThat(output).contains("Avant SampleService.fail()", "Echec SampleService.fail()",
                "Apres SampleService.fail()", "duree=");
    }

    @TestConfiguration(proxyBeanMethods = false)
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    @Import({MetierImpl.class, LogAspect.class, AuthorizationAspect.class})
    static class CglibConfiguration {
    }

    public static class SampleService {
        static final IllegalStateException FAILURE = new IllegalStateException("Echec metier");

        public String unmarked() {
            return "sans journalisation";
        }

        @Log
        public void fail() {
            throw FAILURE;
        }
    }
}
