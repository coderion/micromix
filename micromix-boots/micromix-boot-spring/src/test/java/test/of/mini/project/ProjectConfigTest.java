package test.of.mini.project;

import micromix.MicroMixBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ProjectConfigTest.class)
@ComponentScan
@EnableAutoConfiguration
public class ProjectConfigTest extends ProjectConfig {

    // Test level beans

    @Bean
    String testBean() {
        return "testBean";
    }

    // Collaborators

    @Autowired
    ProjectBean projectBean;

    @Autowired
    Date projectConfigBean;

    @Autowired
    MicroMixBean microMixBean;

    @Autowired
    String testBean;

    // Tests

    @Test
    public void shouldDetectProjectBean() {
        assertNotNull(projectBean);
    }

    @Test
    public void shouldDetectProjectConfigBean() {
        assertNotNull(projectConfigBean);
    }

    @Test
    public void shouldDetectMicroMixBean() {
        assertNotNull(microMixBean);
    }

    @Test
    public void shouldDetectTestBean() {
        assertNotNull(testBean);
    }

}