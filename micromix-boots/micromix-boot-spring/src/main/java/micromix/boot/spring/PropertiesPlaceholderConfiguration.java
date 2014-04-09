package micromix.boot.spring;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class PropertiesPlaceholderConfiguration {

    @Bean
    static public PropertyPlaceholderConfigurer myPropertySourcesPlaceholderConfigurer() {
        PropertyPlaceholderConfigurer propertiesConfigurer = new PropertyPlaceholderConfigurer();
        propertiesConfigurer.setLocation(new ClassPathResource("/micromix/boot/spring/micromix.properties"));
        propertiesConfigurer.setIgnoreResourceNotFound(true);
        propertiesConfigurer.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
        return propertiesConfigurer;
    }

}