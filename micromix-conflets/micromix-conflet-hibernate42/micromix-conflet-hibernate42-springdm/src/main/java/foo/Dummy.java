package foo;

import micromix.conflet.hibernate42.spring.HibernateConfiguration;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;

public class Dummy {

    HibernateConfiguration hibernateConfiguration;
    ConfigurationClassPostProcessor commonAnnotationBeanPostProcessor;
    AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor;

}
