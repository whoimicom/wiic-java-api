package kim.kin.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 *
 * @author kin.kim
 * @Date 2021-09-28
 */
public class HttpClientTest {
    public void test1() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://www.baidu.com")).build();
        HttpResponse.BodyHandler<String> responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, responseBodyHandler);
        String body = response.body();
        System.out.println(body);

        HttpResponse.BodyHandler<InputStream> inputStreamBodyHandler = HttpResponse.BodyHandlers.ofInputStream();
        HttpResponse<InputStream> httpResponse = client.send(request, inputStreamBodyHandler);
        InputStream inputStream = httpResponse.body();
        FileOutputStream fis = new FileOutputStream("src/index.html");
        inputStream.transferTo(fis);

    }

}
