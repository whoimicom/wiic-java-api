package kim.kin.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;


@SuppressWarnings("ALL")
public class ImageBase64Utils {

    /**
     * @param imgPath imgPath
     * @return base64String
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String getImageStr(String imgPath) throws IOException {
        String base64Str = null;

//        try (InputStream in = new FileInputStream(imgPath)) {
//            byte[] bytes = new byte[in.available()];
//            in.read(data);
//            base64Str = Base64.getEncoder().encodeToString(bytes);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        byte[] bytes = Files.readAllBytes(Paths.get(imgPath));
        base64Str = Base64.getEncoder().encodeToString(bytes);

        return base64Str;
    }

    /**
     * base64字符串转化成图片
     *
     * @param base64Str   图片编码
     * @param imgFilePath 存放到本地路径
     * @return
     * @throws IOException
     */
    @SuppressWarnings("finally")
    public static boolean base64Str2Image(String base64Str, String imgFilePath) throws IOException {
        boolean flag = false;
        if (Optional.of(base64Str).isPresent() && Optional.ofNullable(imgFilePath).isPresent()) {
//            Files.write(Paths.get(imgFilePath), Base64.getDecoder().decode(base64Str));
            try (OutputStream out = new FileOutputStream(imgFilePath)) {
                byte[] b = Base64.getDecoder().decode(base64Str);
                for (int i = 0; i < b.length; ++i) {
                    if (b[i] < 0) {
                        b[i] += 256;
                    }
                }
                out.write(b);
                out.flush();
                flag = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        String s = ImageBase64Utils.getImageStr("D:\\2.jpg");
        base64Str2Image(s,"d:\\2.3.jpg");
//        System.out.println(s);
  /*      HashMap<String, Object> map = new HashMap<>();
        String[] value = {s};
        String[] image = {"image"};
        map.put("key", image);
        map.put("value", value);
        ObjectMapper objectMapper = new ObjectMapper();
        String string = HttpKimUtils.getInstance().postJson("http://hw.5dhj.com:9998/ocr/prediction", objectMapper.writeValueAsString(map));
        System.out.println(string);*/
    }
}