package kim.kin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author choky
 */
@SpringBootApplication
@RestController()
public class PromgtApplication {

    public static void main(String[] args) {
//        SpringApplication.run(PromgtApplication.class, args);
        ConfigurableApplicationContext context = SpringApplication.run(PromgtApplication.class, args);
        Environment environment = context.getBean(Environment.class);
        String port = environment.getProperty("server.port");
        String contextPath = environment.getProperty("server.servlet.context-path");
        String webPath = environment.getProperty("kim.kin.web-path");
        System.out.println("api>  http://localhost:" + port + contextPath);
        System.out.println("web>  http://localhost"+webPath);
    }

}
