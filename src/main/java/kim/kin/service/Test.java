package kim.kin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;

@Service
public class Test {
    @Autowired
    private EntityManager em;
    @Autowired
    DataSourceProperties dataSourceProperties;
    public void test() throws Exception {
        em.getEntityManagerFactory().getProperties().forEach((s, o) -> System.out.println(s+" "+o));
        em.getProperties().forEach((s, o) -> System.out.println(s+" "+o));
        em.getMetamodel().getEmbeddables();
        printAllFields(dataSourceProperties);
    }
    public static void printAllFields(Object obj){
        Class    cls=obj.getClass();
        Field[] fields=cls.getDeclaredFields();
        System.out.println("共有"+fields.length+"个属性");
        for (Field field:fields) {
            field.setAccessible(true);
            try {
                System.out.println(field.getName()+":"+field.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
