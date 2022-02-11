package kim.kin.utils;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author choky
 */
@SuppressWarnings("ClassCanBeRecord")
@Service
public class DataSourcePropertiesUtil {
    private final DataSourceProperties dataSourceProperties;
    private final DataSourceTransactionManager dataSourceTransactionManager;

    public DataSourcePropertiesUtil(DataSourceProperties dataSourceProperties, DataSourceTransactionManager dataSourceTransactionManager) {
        this.dataSourceProperties = dataSourceProperties;
        this.dataSourceTransactionManager = dataSourceTransactionManager;
    }

    public void showDatabaseMetaData() throws SQLException {
        DatabaseMetaData metaData = Objects.requireNonNull(dataSourceTransactionManager.getDataSource()).getConnection().getMetaData();
        System.out.println(String.format("%1$-" + 27 + "s", "databaseProductName:") + metaData.getDatabaseProductName());
        System.out.println(String.format("%1$-" + 27 + "s", "getDatabaseProductVersion:") + metaData.getDatabaseProductVersion());
        System.out.println(String.format("%1$-" + 27 + "s", "getDatabaseMajorVersion:") + metaData.getDatabaseMajorVersion());
        System.out.println(String.format("%1$-" + 27 + "s", "getDatabaseMinorVersion:") + metaData.getDatabaseMinorVersion());
        System.out.println(String.format("%1$-" + 27 + "s", "getJDBCMajorVersion:") + metaData.getJDBCMajorVersion());
        System.out.println(String.format("%1$-" + 27 + "s", "getJDBCMinorVersion:") + metaData.getJDBCMinorVersion());
        printAllFields(dataSourceProperties);
    }

    public static void printAllFields(Object obj) {
        Class<?> cls = obj.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                System.out.println(String.format("%1$-" + 27 + "s", field.getName() + ":") + field.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
