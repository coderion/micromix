package micromix.conflet.hibernate42.spring;

import micromix.boot.spring.SpringBoot;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

public class HibernateConfigurationTest extends Assert {

    SpringBoot boot = new SpringBoot() {
        @Override
        public Map<String, Object> properties() {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("micromix.conflet.hibernate42.spring.persistenceUnitName", "PERSON");
            return m;
        }
    };

    @Test
    public void xxx() {
        EntityManagerFactory entityManagerFactory = boot.context().getBean(EntityManagerFactory.class);
        assertNotNull(entityManagerFactory);
    }

}
