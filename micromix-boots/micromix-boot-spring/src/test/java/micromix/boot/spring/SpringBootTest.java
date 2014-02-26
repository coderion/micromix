package micromix.boot.spring;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class SpringBootTest extends Assert {

    String beanName = "beanName";

    SpringBoot springBoot = new SpringBoot() {
        @Override
        public Map<String, Class<?>> beans() {
            Map<String, Class<?>> beans = newHashMap();
            beans.put(beanName, Bean.class);
            return beans;
        }
    };

    @Test
    public void shouldCreateSpringContext() {
        assertNotNull(springBoot.context());
    }

    @Test
    public void shouldRegisterBean() {
        Bean bean = springBoot.context().getBean(beanName, Bean.class);
        assertNotNull(bean);
    }

}
