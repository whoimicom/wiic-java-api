package kim.kin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController()
public class KkApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(KkApiApplication.class, args);
	}

	@PostMapping("/")
	public String applicationSucess(){
		return "applicationSucess";
	}

}
