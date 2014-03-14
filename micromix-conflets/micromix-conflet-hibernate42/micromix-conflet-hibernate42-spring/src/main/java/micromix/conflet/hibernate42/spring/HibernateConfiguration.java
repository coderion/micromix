package micromix.conflet.hibernate42.spring;

import org.osgi.framework.FrameworkUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

import static javax.persistence.Persistence.createEntityManagerFactory;

@Configuration
public class HibernateConfiguration implements IHibernateConfiguration {

    @Value("${micromix.conflet.hibernate42.spring.persistenceUnitName:MICROMIX}")
    String persistenceUnitName;

    @Bean
    EntityManagerFactory entityManagerFactory() {
        if (FrameworkUtil.getBundle(getClass()) != null) {
            return null;
        }
        return createEntityManagerFactory(persistenceUnitName);
    }

}
