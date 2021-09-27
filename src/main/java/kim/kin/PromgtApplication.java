package kim.kin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author choky
 */
@SpringBootApplication
@RestController()
public class PromgtApplication {

    public static void main(String[] args) {
        SpringApplication.run(PromgtApplication.class, args);
    }

    @GetMapping("/")
    public String index() {
        return "kk service started successfully";
    }

}
