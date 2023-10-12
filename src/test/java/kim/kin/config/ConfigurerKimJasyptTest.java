package kim.kin.config;

import kim.kin.utils.DataSourcePropertiesUtil;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ConfigurerKimJasyptTest {
    @Autowired
    ConfigurableEnvironment environment;
    @Autowired
    DataSourcePropertiesUtil dataSourcePropertiesUtil;

    @Test
    public void testEnvironmentProperties() throws SQLException {
        assertEquals("kin.kim", environment.getProperty("spring.datasource.username"));
        dataSourcePropertiesUtil.showDatabaseMetaData();
    }
}
