package micromix.boot.spring;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class MicroMixApplicationBuilder {

    private Class<? extends AutoConfiguration> applicationConfig = AutoConfiguration.class;

    private boolean web = false;

    public ConfigurableApplicationContext build() {
        return toSpringApplicationBuilder().run();
    }

    public SpringApplicationBuilder toSpringApplicationBuilder() {
        return new SpringApplicationBuilder().sources(applicationConfig).web(web);
    }

    public MicroMixApplicationBuilder applicationConfig(Class<? extends AutoConfiguration> applicationConfig) {
        this.applicationConfig = applicationConfig;
        return this;
    }

    public MicroMixApplicationBuilder web(boolean web) {
        this.web = web;
        return this;
    }

}
