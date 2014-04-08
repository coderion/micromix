package micromix.boot.spring;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;

public class SpringBootWithParentContextTest extends Assert {

    SpringBoot springBoot = new SpringBoot() {

        @Override
        public ApplicationContext parentContext() {
            GenericApplicationContext context = new GenericApplicationContext();
            ConfigurableListableBeanFactory beanRegistry = (ConfigurableListableBeanFactory) context.getAutowireCapableBeanFactory();
            beanRegistry.registerSingleton("bar", "bar");
            context.refresh();
            return context;
        }

        @Override
        public List<?> singletons() {
            return asList(new Date());
        }

    };

    @Test
    public void shouldLoadBeanFromPrimaryContext() {
        assertNotNull(springBoot.context().getBean(Date.class));
    }

    @Test
    public void shouldLoadBeanFromParentContext() {
        assertNotNull(springBoot.context().getBean("bar"));
    }

}
