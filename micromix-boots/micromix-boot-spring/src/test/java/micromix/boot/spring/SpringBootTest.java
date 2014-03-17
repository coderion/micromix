package micromix.boot.spring;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Arrays.asList;

public class SpringBootTest extends Assert {

    String beanName = "beanName";

    String stringBean = "stringBean";

    SpringBoot springBoot = new SpringBoot() {
        @Override
        public Map<String, Class<?>> namedBeansDefinitions() {
            Map<String, Class<?>> beans = newHashMap();
            beans.put(beanName, Bean.class);
            return beans;
        }

        @Override
        public List<?> singletons() {
            return asList(stringBean, new BeanHolder());
        }
    };

    @Test
    public void shouldCreateSpringContext() {
        assertNotNull(springBoot.context());
    }

    @Test
    public void shouldRegisterBeanDefinition() {
        Bean bean = springBoot.context().getBean(beanName, Bean.class);
        assertNotNull(bean);
    }

    @Test
    public void shouldRegisterBean() {
        String stringBean = springBoot.context().getBean(String.class);
        assertEquals(this.stringBean, stringBean);
    }

    @Test
    public void shouldAutowireRegisteredBean() {
        BeanHolder beanHolder = springBoot.context().getBean(BeanHolder.class);
        assertNotNull(beanHolder.bean);
    }

}
