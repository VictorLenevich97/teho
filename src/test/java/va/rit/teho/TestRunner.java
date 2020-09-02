package va.rit.teho;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("va.rit.teho.controller")
@ComponentScan("va.rit.teho.service")
public class TestRunner {

    public static void main(String[] args) {
        SpringApplication.run(TestRunner.class);
    }
}
