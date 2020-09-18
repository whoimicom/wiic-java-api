package kim.kin;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController()
@EnableEncryptableProperties
public class KkApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(KkApiApplication.class, args);
    }

    @GetMapping("/")
    public String index() {
        return "kk service started successfully";
    }

}
