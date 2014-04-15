package micromix.boot.spring;

import micromix.MicroMixScanner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public abstract class AutoConfiguration extends MicroMixScanner {
}