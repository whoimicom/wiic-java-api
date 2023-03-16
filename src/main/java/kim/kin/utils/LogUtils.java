package kim.kin.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Base64;
import java.util.HashMap;

public class LogUtils {
    public static void main(String[] args) throws IOException {
        record ChangeMethod(String ip, String methodName, String requestUrl) {
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("192.168.0.242", "huij-hapi");
        map.put("192.168.0.135", "huij-hapi");
        map.put("192.168.0.61", "fireway");
        map.put("192.168.0.220", "fireway");
        map.put("192.168.0.167", "operate");
        map.put("192.168.0.169", "operate");
        map.put("192.168.0.132", "hoben-api");
        map.put("192.168.0.244", "hoben-api");
        map.put("192.168.0.150", "huihua");
        map.put("192.168.0.180", "huij-integral");
        map.put("192.168.0.50", "huij-pay");
        map.put("192.168.0.228", "huij-sign");
        map.put("192.168.0.121", "huij-telmkt");
        HashMap<String, ChangeMethod> mapMethod = new HashMap<>();
        Files.lines(Paths.get("d:\\h5memberLogs")).forEach(s -> {
//            String a = "2023-03-15 09:51:40method:com.dragon.core.web.spring.TokenInterceptor.preHandle(TokenInterceptor.java:26)IP:192.168.0.135 RequestURL:http://192.168.0.3/finance/intergral/tanKuangTS.do MethodName:com.dragon.site.action.JFserialAction#tanKuangTS";
            String[] split = s.split("IP:|RequestURL:|MethodName:");
            if (split.length > 3) {
                String ip = split[1].trim();
                String requestUrl = split[2].trim();
                String methodName = split[3].trim();
                if (map.containsKey(ip)) {
                    ip = map.get(ip);
                }
                String base64 = Base64.getEncoder().encodeToString((methodName + ip).getBytes(StandardCharsets.UTF_8));
                if (!mapMethod.containsKey(base64)) {
                    mapMethod.put(base64, new ChangeMethod(ip, methodName, requestUrl));
                }
            } else {
                System.err.println(s);
            }
        });
        String fileUrl = "d:\\out.sql";
        Path path = Paths.get(fileUrl);
        Files.deleteIfExists(path);
        Files.createFile(path);
        mapMethod.keySet().stream().sorted(String::compareTo).forEach(s -> {
            ChangeMethod c = mapMethod.get(s);
            String sql = "insert into test (host,method_name,request_url,base_method) values( '" + c.ip + "',  '" + c.methodName + "','" + c.requestUrl + "', '" + s + "' );\n ";
            try {
//                Files.write(path, sql.getBytes());
                Files.writeString(path,sql, StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(sql);
        });
//        select *  FROM hoben.test WHERE id NOT IN (SELECT t.id FROM ( SELECT MIN(id) as id FROM hoben.test GROUP BY base_method ) t);
    }

}
