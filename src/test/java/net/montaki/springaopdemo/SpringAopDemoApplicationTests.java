package net.montaki.springaopdemo;

import net.montaki.springaopdemo.security.SecurityContext;
import net.montaki.springaopdemo.service.IMetier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {"demo.enabled=false", "logging.file.name=target/spring-aop-test.log"})
@ExtendWith(OutputCaptureExtension.class)
class SpringAopDemoApplicationTests {
    @Autowired
    private IMetier metier;

    @AfterEach
    void clearSecurityContext() {
        SecurityContext.clear();
    }

    @Test
    void springInjectsAJdkProxy() {
        assertThat(AopUtils.isJdkDynamicProxy(metier)).isTrue();
    }

    @Test
    void anonymousCannotExecuteBusinessCode(CapturedOutput output) {
        assertThatThrownBy(metier::process).isInstanceOf(SecurityException.class)
                .hasMessageContaining("Authentification requise");
        assertThat(output).doesNotContain("Business Process").doesNotContain("Avant IMetier.process");
    }

    @Test
    void userCanProcessButCannotCompute(CapturedOutput output) {
        SecurityContext.authenticate("user", "1234");
        metier.process();
        assertThatThrownBy(metier::compute).isInstanceOf(SecurityException.class)
                .hasMessageContaining("compute");
        assertThat(output).contains("Business Process", "Avant IMetier.process()", "Apres IMetier.process()")
                .doesNotContain("Business Computing");
    }

    @Test
    void adminGetsTheOriginalReturnValueAndExecutionTime(CapturedOutput output) {
        SecurityContext.authenticate("root", "1234");
        metier.process();
        assertThat(metier.compute()).isEqualTo(42.0);
        assertThat(output).contains("Business Computing", "Avant IMetier.compute()", "Apres IMetier.compute()", "duree=");
    }

    @Test
    void failedAuthenticationClearsPreviousIdentity() {
        SecurityContext.authenticate("root", "1234");
        assertThatThrownBy(() -> SecurityContext.authenticate("root", "incorrect"))
                .isInstanceOf(SecurityException.class);
        assertThat(SecurityContext.isAuthenticated()).isFalse();
        assertThat(SecurityContext.hasRole("ADMIN")).isFalse();
        assertThatThrownBy(metier::compute).isInstanceOf(SecurityException.class);
    }

}
