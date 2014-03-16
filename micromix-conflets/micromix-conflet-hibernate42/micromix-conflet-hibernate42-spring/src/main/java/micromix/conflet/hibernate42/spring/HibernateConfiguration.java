package micromix.conflet.hibernate42.spring;

import org.osgi.framework.FrameworkUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import static javax.persistence.Persistence.createEntityManagerFactory;

@Configuration
public class HibernateConfiguration implements IHibernateConfiguration {

    @Value("${micromix.conflet.hibernate42.spring.persistenceUnitName:micromix}")
    String persistenceUnitName;

    @Bean
    EntityManagerFactory entityManagerFactory() {
        if (FrameworkUtil.getBundle(getClass()) != null) {
            return null;
        }

        AbstractEntityManagerFactoryBean f = new AbstractEntityManagerFactoryBean() {
            @Override
            protected EntityManagerFactory createNativeEntityManagerFactory() throws PersistenceException {
                return createEntityManagerFactory(persistenceUnitName);
            }
        };
        f.afterPropertiesSet();
        return f.getObject();
    }

}
