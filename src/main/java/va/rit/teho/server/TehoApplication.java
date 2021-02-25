package va.rit.teho.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@SpringBootApplication
@ComponentScan(value = "va.rit.teho.controller", lazyInit = true)
@ComponentScan(value = "va.rit.teho.service", lazyInit = true)
@ConfigurationPropertiesScan("va.rit.teho.server.config")
@EntityScan("va.rit.teho.entity")
@EnableJpaRepositories(value = "va.rit.teho.repository", bootstrapMode = BootstrapMode.LAZY)
public class TehoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TehoApplication.class);
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

}
