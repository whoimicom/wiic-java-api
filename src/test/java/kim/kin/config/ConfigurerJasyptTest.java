package kim.kin.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfigurerJasyptTest {
    @Autowired
    ConfigurableEnvironment environment;
    @Test
    public void testEnvironmentProperties() {
        String location = environment.getProperty("jasypt.encryptor:location");
        System.out.println(location);
        System.out.println(environment.getProperty("jasypt.encryptor:tlocation1"));
        System.out.println(environment.getProperty("jasypt.encryptor:tlocation2"));
        System.out.println(environment.getProperty("jasypt.encryptor:tlocation3"));
        assertEquals("chupacabras", location);
        assertEquals("chupacabras", environment.getProperty("secret2.property"));
        assertEquals("chupacabras", environment.getProperty("secret3.property"));
    }
}
