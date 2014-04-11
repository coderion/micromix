package micromix.boot.spring;

import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Map;

public class AnnotatedBeanDefinitionApplicationContextInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

    private final Map<String, Class<?>> beanDefinitions;

    public AnnotatedBeanDefinitionApplicationContextInitializer(Map<String, Class<?>> beanDefinitions) {
        this.beanDefinitions = beanDefinitions;
    }

    @Override
    public void initialize(GenericApplicationContext applicationContext) {
        for (Map.Entry<String, Class<?>> beanDefinition : beanDefinitions.entrySet()) {
            applicationContext.registerBeanDefinition(beanDefinition.getKey(), new AnnotatedGenericBeanDefinition(beanDefinition.getValue()));
        }
    }

}