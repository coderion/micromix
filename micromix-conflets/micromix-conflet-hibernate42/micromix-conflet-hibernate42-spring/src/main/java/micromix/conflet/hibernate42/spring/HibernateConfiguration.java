package micromix.conflet.hibernate42.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

import static javax.persistence.Persistence.createEntityManagerFactory;

@Configuration
public class HibernateConfiguration {

    @Value("${micromix.conflet.hibernate42.spring.persistenceUnitName:micromix}")
    String persistenceUnitName;

    @Bean
    EntityManagerFactory entityManagerFactory() {
        return createEntityManagerFactory(persistenceUnitName);
    }

}
