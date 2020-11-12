package va.rit.teho.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("va.rit.teho.controller")
@ComponentScan("va.rit.teho.service")
@ConfigurationPropertiesScan("va.rit.teho.server.config")
@EntityScan("va.rit.teho.entity")
@EnableJpaRepositories("va.rit.teho.repository")
public class TehoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TehoApplication.class);
    }
}
