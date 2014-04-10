package micromix.conflet.hibernate42.springdm;

import fuse.pocs.blueprint.openjpa.route.Person;
import org.apache.camel.CamelContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import javax.inject.Inject;
import java.io.File;

import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureConsole;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;
import static org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;

@RunWith(PaxExam.class)
public class Hibernate42CamelTest extends Assert {

    @Inject
    CamelContext camelContext;

    @Configuration
    public Option[] commonOptions() {

        return new Option[]{
                karafDistributionConfiguration()
                        .frameworkUrl(
                                maven().groupId("org.apache.karaf")
                                        .artifactId("apache-karaf")
                                        .type("zip")
                                        .version("2.3.3"))
                        .karafVersion("2.3.3")
                        .name("Apache Karaf")
                        .unpackDirectory(new File("build/pax"))
                        .useDeployFolder(false),

                keepRuntimeFolder(),
                configureConsole().ignoreLocalConsole(),
                logLevel(LogLevel.INFO),


                features(
                        maven().groupId("org.apache.karaf.assemblies.features").artifactId("enterprise").type("xml")
                                .classifier("features").version("2.3.3"), "transaction", "jndi", "jpa", "spring", "spring-orm"),

                features(
                        maven().groupId("org.apache.camel.karaf").artifactId("apache-camel").
                                type("xml").classifier("features").version("2.12.2"),
                        "camel-blueprint"),

//                debugConfiguration("5005", true),

                bundle("mvn:org.apache.camel/camel-jpa/2.12.2"),

                mavenBundle().groupId("org.hsqldb").artifactId("hsqldb").version("2.3.2"),
                mavenBundle().groupId("com.github.micromix").artifactId("micromix-conflet-hibernate42-springdm-itest-datasource").version("0.9-SNAPSHOT"),

                bundle("mvn:com.fasterxml/classmate/0.9.0"),
                bundle("mvn:org.apache.geronimo.specs/geronimo-servlet_3.0_spec/1.0"),
                bundle("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.antlr/2.7.7_5"),
                bundle("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.ant/1.8.2_2"),
                bundle("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.dom4j/1.6.1_5"),
                bundle("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.serp/1.14.1_1"),
                bundle("mvn:org.javassist/javassist/3.18.1-GA"),
                bundle("mvn:org.jboss.spec.javax.security.jacc/jboss-jacc-api_1.4_spec/1.0.2.Final"),
                bundle("wrap:mvn:org.jboss/jandex/1.1.0.Final"),
                bundle("mvn:org.jboss.logging/jboss-logging/3.1.3.GA"),

                bundle("mvn:org.hibernate.common/hibernate-commons-annotations/4.0.4.Final"),

                mavenBundle().groupId("org.hibernate").artifactId("hibernate-core").version("4.2.9.Final"),
                mavenBundle().groupId("org.hibernate").artifactId("hibernate-entitymanager").version("4.2.9.Final"),


                mavenBundle().groupId("com.github.micromix").artifactId("micromix-conflet-hibernate42-springdm-itest-route").version("0.9-SNAPSHOT"),
                mavenBundle().groupId("org.hibernate").artifactId("hibernate-osgi").version("4.2.9.Final").startLevel(100),
        };
    }


    @Test
    public void shouldSavePerson() {
        camelContext.createProducerTemplate().sendBody("direct:test", new Person("Henry"));
        Person person = camelContext.createProducerTemplate().requestBody("direct:read", "xxx", Person.class);
        assertEquals("Henry", person.getName());
    }


}
