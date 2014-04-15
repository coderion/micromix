package some.other.packages;

import micromix.MicroMixBean;
import micromix.boot.spring.AutoConfiguration;
import micromix.boot.spring.MicroMixApplicationBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import static org.junit.Assert.assertNotNull;

@ComponentScan
@EnableAutoConfiguration
public class ProjectConfigTest extends AutoConfiguration {

    ApplicationContext defaultContext = new MicroMixApplicationBuilder().build();

    ApplicationContext projectContext;

    @Before
    public void setUp() {
        projectContext = new MicroMixApplicationBuilder().applicationConfig(getClass()).build();
    }

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void shouldNotDetectProjectBeanUsingDefaultConfig() {
        defaultContext.getBean(ProjectBean.class);
    }

    @Test
    public void shouldDetectMicroMixBeanUsingDefaultConfig() {
        assertNotNull(defaultContext.getBean(MicroMixBean.class));
    }

    @Test
    public void shouldDetectProjectBeanUsingProjectConfig() {
        assertNotNull(projectContext.getBean(ProjectBean.class));
    }

    @Test
    public void shouldDetectMicroMixBeanUsingProjectConfig() {
        assertNotNull(projectContext.getBean(MicroMixBean.class));
    }

}