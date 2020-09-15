package va.rit.teho;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EntityScan("va.rit.teho.entity")
@EnableJpaRepositories(basePackages = "va.rit.teho.repository")
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
public class RepositoryTestRunner {

    public static void main(String[] args) {
        SpringApplication.run(RepositoryTestRunner.class);
    }
}
