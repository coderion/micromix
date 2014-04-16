package test.of.mini.project;

import micromix.boot.spring.MicroMixProjectConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Date;

@ComponentScan
@EnableAutoConfiguration
public class ProjectConfig extends MicroMixProjectConfiguration {

    @Bean
    Date projectConfigBean() {
        return new Date();
    }

}