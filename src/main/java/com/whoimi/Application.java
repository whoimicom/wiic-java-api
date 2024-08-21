package com.whoimi;

import org.springframework.core.env.Environment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author whoimi
 * @since 2021-10-28
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
//        SpringApplication.run(KimApplication.class, args);

        System.setProperty("spring.devtools.restart.enabled", "false");
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        Environment environment = context.getBean(Environment.class);
        String port = environment.getProperty("server.port");
        String springApplicationName = environment.getProperty("spring.application.name");
        String webPath = environment.getProperty("kim.kin.web-path");
        System.out.println("wii-java-api>  http://localhost:" + port + "/" + springApplicationName);
        System.out.println("wii-java-admin>  http://localhost" + webPath);
    }

}
