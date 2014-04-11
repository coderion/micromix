package micromix.boot.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

public class ScanPackagesApplicationContextInitializer implements ApplicationContextInitializer {

    private final String[] basePackages;

    public ScanPackagesApplicationContextInitializer(String... basePackages) {
        this.basePackages = basePackages;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner((BeanDefinitionRegistry) applicationContext, true);
        scanner.setResourceLoader(applicationContext);
        scanner.scan(basePackages);
    }

}