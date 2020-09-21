package kim.kin.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleAsymmetricConfig;
import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleAsymmetricStringEncryptor;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.util.text.AES256TextEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static com.ulisesbocchio.jasyptspringboot.util.AsymmetricCryptography.KeyFormat.DER;
import static com.ulisesbocchio.jasyptspringboot.util.AsymmetricCryptography.KeyFormat.PEM;


@Configuration
@EnableEncryptableProperties
public class ConfigurerJasypt {

    @Value("${jasypt.encryptor:tlocation}")
    private String tlocation;

    public String getTlocation() {
        return tlocation;
    }

    public void setTlocation(String tlocation) {
        this.tlocation = tlocation;
    }




    public static void main(String[] args) throws IOException {
        String salt = "a2lua2lt";
        String username = "kinkim";
        String password = "123456";

        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(salt);
        System.out.println("BasicTextEncryptor encryUsername:" + textEncryptor.encrypt(username));
        System.out.println("BasicTextEncryptor encryPassword:" + textEncryptor.encrypt(password));


        AES256TextEncryptor aes256TextEncryptor = new AES256TextEncryptor();
        aes256TextEncryptor.setPassword(salt);
        System.out.println("AES256TextEncryptor encryPassword:" + aes256TextEncryptor.encrypt(username));
        System.out.println("AES256TextEncryptor encryPassword:" + aes256TextEncryptor.encrypt(password));

//        String publicKeyLocation = Files.lines(Paths.get(System.getProperty("user.home") + "/.ssh/id_rsa"), StandardCharsets.UTF_8)
        String separator = System.getProperty("line.separator");
        String privateKey = Files.lines(Paths.get(System.getProperty("user.home") + "/.ssh/id_rsa_pkcs8"), StandardCharsets.UTF_8)
                .collect(Collectors.joining(separator));
//                .collect(Collectors.joining());
//                .collect(Collectors.joining("\\n"));
        String publicKey = Files.lines(Paths.get(System.getProperty("user.home") + "/.ssh/id_rsa_pkcs8.pem"), StandardCharsets.UTF_8)
                .collect(Collectors.joining(separator));
//                .collect(Collectors.joining());
//                .collect(Collectors.joining("\\n"));
        System.out.println(privateKey);
        System.out.println(publicKey);


        SimpleAsymmetricConfig config = new SimpleAsymmetricConfig();
        config.setKeyFormat(PEM);
//        config.setPublicKey("-----BEGIN PUBLIC KEY-----\n" +
//                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArQfyGCvBOdgmDGU6ciGP\n" +
//                "VNB6jHsMip0b0qOrPvVTSJ/x0offjKARogA2tjGjyr3rUtwg9woMBqv/iyENR0GB\n" +
//                "nIUa0jkYsznCKeygcflnNa4mrVf7XKXLhSwtY+kCe3diPk+0QPfEsfF9/aK6pWBU\n" +
//                "FcrE8P2k2sF/8mo8dFJU1t6zQGPspHkNAgR6MLU8SjPZxnMS6EG722MdYhvSYAKs\n" +
//                "nu02Hozqb4jh/gaQ/E6NkvM3DkqIyIYsRH2smstIFEb9CCiTdiz/OsJKQLgGy/pq\n" +
//                "IVKtai3lnUxAayEV45Z61rNTOusNJf+icGhZxjqhAeoWjMxOCVmVC2GKa9sisqBg\n" +
//                "kQIDAQAB\n" +
//                "-----END PUBLIC KEY-----\n");
        config.setPrivateKey(privateKey + separator);
        config.setPublicKey(publicKey + separator);

        StringEncryptor stringEncryptor = new SimpleAsymmetricStringEncryptor(config);
        String encryptUsername = stringEncryptor.encrypt(username);
        String encryptPassword = stringEncryptor.encrypt(password);
        String decryptUsername = stringEncryptor.decrypt(encryptUsername);
        String decryptPassword = stringEncryptor.decrypt(encryptPassword);
        System.out.println("SimpleAsymmetricStringEncryptor entryUsername:" + encryptUsername);
        System.out.println("SimpleAsymmetricStringEncryptor entryPassword:" + encryptPassword);
        System.out.println("SimpleAsymmetricStringEncryptor decryptUsername:" + decryptUsername);
        System.out.println("SimpleAsymmetricStringEncryptor decryptPassword:" + decryptPassword);


    }

}
