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
        assertEquals("kinkim", environment.getProperty("spring.datasource.username"));
    }
}
