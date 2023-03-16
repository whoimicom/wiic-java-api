package kim.kin.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 *
 * @author kin.kim
 * @since  2021-09-28
 */
@SuppressWarnings("unused")
public class HttpClientTest {
    public void test1() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://www.baidu.com")).build();
        HttpResponse.BodyHandler<String> responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, responseBodyHandler);
        String body = response.body();
        System.out.println(body);

        HttpResponse.BodyHandler<InputStream> inputStreamBodyHandler = HttpResponse.BodyHandlers.ofInputStream();
        HttpResponse<InputStream> httpResponse = client.send(request, inputStreamBodyHandler);
        try (InputStream inputStream = httpResponse.body()) {
            FileOutputStream fis = new FileOutputStream("src/index.html");
            inputStream.transferTo(fis);
        }

        String s = Base64.getEncoder().encodeToString("com.dragon.site.action.H5HBCashAction#getProductforlevelhoben-api".getBytes(StandardCharsets.UTF_8));
        System.out.println(s);

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClientTest httpClientTest = new HttpClientTest();
        httpClientTest.test1();

    }

}
