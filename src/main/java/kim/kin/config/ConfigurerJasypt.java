package kim.kin.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.util.text.BasicTextEncryptor;



@EnableEncryptableProperties
public class ConfigurerJasypt {

    public static void main(String[] args) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        //加密所需的salt(盐)
        textEncryptor.setPassword("a2luLmtpbQ==");
        //要加密的数据（数据库的用户名或密码）
        String username = textEncryptor.encrypt("kinkim");
        String password = textEncryptor.encrypt("123456");
        System.out.println("username:"+username);
        System.out.println("password:"+password);
    }

}
