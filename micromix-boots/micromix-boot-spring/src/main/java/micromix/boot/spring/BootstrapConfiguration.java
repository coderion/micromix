package micromix.boot.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(value = "classpath:xxx.xml")
public class BootstrapConfiguration {

}