package kim.kin.utils;

import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


/**
 * @author choky
 */
@Component
public class HttpKimUtils {
    private static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded;charset=utf-8";
    private static final String CONTENT_TYPE_JSON = "application/json; charset=utf-8";
    private static final String CONTENT_TYPE_STREAM = "application/octet-stream; charset=utf-8";
    private static final String CHARSET = "utf-8";
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 15000;
    private static HttpKimUtils instance = null;
    private static final String LINE_SEPARATOR = System.lineSeparator();

    public static HttpKimUtils getInstance() {
        if (instance == null) {
            return new HttpKimUtils();
        }
        return instance;
    }

    /**
     * POST 以application/x-www-form-urlencoded;charset=utf-8方式传输
     *
     * @param url
     * @param headermap
     * @return
     * @throws SocketTimeoutException
     * @throws IOException
     */
    public String postForm(String url, Map<String, String> headermap) throws SocketTimeoutException, IOException, NoSuchAlgorithmException, KeyManagementException {
        return doRequest("POST", url, "", CONNECT_TIMEOUT, READ_TIMEOUT, CONTENT_TYPE_FORM, headermap);
    }

    /**
     * POST 以application/x-www-form-urlencoded;charset=utf-8方式传输
     *
     * @param url
     * @param params
     * @param headermap
     * @return
     * @throws SocketTimeoutException
     * @throws IOException
     */
    public String postForm(String url, Map<String, String> params, Map<String, String> headermap) throws SocketTimeoutException, IOException, NoSuchAlgorithmException, KeyManagementException {
        return doRequest("POST", url, buildQuery(params), CONNECT_TIMEOUT, READ_TIMEOUT, CONTENT_TYPE_FORM, headermap);
    }

    /**
     * POST 以application/x-www-form-urlencoded;charset=utf-8方式传输
     *
     * @param url
     * @return
     * @throws SocketTimeoutException
     * @throws IOException
     */
    public String getForm(String url, Map<String, String> headermap) throws SocketTimeoutException, IOException, NoSuchAlgorithmException, KeyManagementException {
        return doRequest("GET", url, "", CONNECT_TIMEOUT, READ_TIMEOUT, CONTENT_TYPE_FORM, headermap);
    }

    /**
     * POST 以application/x-www-form-urlencoded;charset=utf-8方式传输
     *
     * @param url
     * @param params
     * @param headermap
     * @return
     * @throws SocketTimeoutException
     * @throws IOException
     */
    public String getForm(String url, Map<String, String> params, Map<String, String> headermap) throws SocketTimeoutException, IOException, NoSuchAlgorithmException, KeyManagementException {
        return doRequest("GET", url, buildQuery(params), CONNECT_TIMEOUT, READ_TIMEOUT, CONTENT_TYPE_FORM, headermap);
    }

    /**
     * @param method         请求的method post/get
     * @param url            请求url
     * @param requestContent 请求参数
     * @param connectTimeout 请求超时
     * @param readTimeout    响应超时
     * @param contentType    请求格式
     * @param headerMap      请求header中要封装的参数
     * @return
     * @throws SocketTimeoutException
     * @throws IOException
     * @Title doRequest
     */
    private String doRequest(String method, String url, String requestContent, int connectTimeout, int readTimeout, String contentType, Map<String, String> headerMap) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        HttpURLConnection conn = getConnection(new URL(url), method, contentType, headerMap);
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        if (requestContent != null && requestContent.trim().length() > 0) {
            try (OutputStream out = conn.getOutputStream()) {
                out.write(requestContent.getBytes(CHARSET));
            }
        }
        return getResponseAsString(conn);

    }

    public String doRequest(String method, String url, byte[] bytes, int connectTimeout, int readTimeout, String contentType, Map<String, String> headerMap) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        HttpURLConnection conn = getConnection(new URL(url), method, contentType, headerMap);
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        if (bytes.length > 0) {
            try (OutputStream out = conn.getOutputStream()) {
                out.write(bytes);
            }
        }
        return getResponseAsString(conn);
    }

    private HttpURLConnection getConnection(URL url, String method, String contentType, Map<String, String> headerMap) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        HttpURLConnection conn;
        if ("https".equals(url.getProtocol())) {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0],
                    new TrustManager[]{new DefaultTrustManager()},
                    new SecureRandom());

            HttpsURLConnection connHttps = (HttpsURLConnection) url.openConnection();
            connHttps.setSSLSocketFactory(ctx.getSocketFactory());
            connHttps.setHostnameVerifier((hostname, session) -> true);
            conn = connHttps;
        } else {
            conn = (HttpURLConnection) url.openConnection();
        }
        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html,application/json");
        conn.setRequestProperty("Content-Type", contentType);
        Optional.ofNullable(headerMap).ifPresent(stringStringMap -> {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        });
        return conn;
    }

    private String getResponseAsString(HttpURLConnection httpURLConnection) throws IOException {
        try (InputStream errorStream = httpURLConnection.getErrorStream();
             InputStream inputStream = httpURLConnection.getInputStream()) {
            int responseCode = httpURLConnection.getResponseCode();
            String responseMessage = httpURLConnection.getResponseMessage();
            System.out.println(responseCode);
            System.out.println(responseMessage);
            String resultStr;
            if (errorStream == null) {
                resultStr = getStreamAsString(inputStream);
            } else {
                resultStr = getStreamAsString(errorStream);
//                throw new IOException(httpURLConnection.getResponseCode() + ":" + httpURLConnection.getResponseMessage());
            }
            return resultStr;
        }

    }

    private String getStreamAsString(InputStream inputStream) throws IOException {
        InputStreamReader reader = new InputStreamReader(inputStream, HttpKimUtils.CHARSET);
        StringBuilder responseStr = new StringBuilder();
        final char[] buff = new char[1024];
        int read = 0;
        while ((read = reader.read(buff)) > 0) {
            responseStr.append(buff, 0, read);
        }
        return responseStr.toString();

    }

    private String buildQuery(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder queryStr = new StringBuilder();
        if (null != params && params.isEmpty()) {
            Set<Map.Entry<String, String>> entries = params.entrySet();
            boolean hasParam = false;
            for (Map.Entry<String, String> entry : entries) {
                String name = entry.getKey();
                String value = entry.getValue();
                if (hasParam) {
                    queryStr.append("&");
                } else {
                    hasParam = true;
                }
                queryStr.append(name).append("=").append(URLEncoder.encode(value, CHARSET));
            }
        }
        return queryStr.toString();

    }

    private class DefaultTrustManager implements X509TrustManager {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
    }

    /**
     * 以application/json; charset=utf-8方式传输
     *
     * @param url
     * @param jsonContent
     * @return
     * @throws SocketTimeoutException
     * @throws IOException
     */
    public String postJson(String url, String jsonContent) throws NoSuchAlgorithmException, IOException, KeyManagementException {
        return doRequest("POST", url, jsonContent, CONNECT_TIMEOUT, READ_TIMEOUT, CONTENT_TYPE_JSON, null);
    }

    public String postFiles(String actionUrl, String[] uploadFilePaths, Map<String, String> headerMap) throws Exception {
        String twoHyphens = "--";
        String boundary = "*****";
        StringBuilder stringBuilder = new StringBuilder();
        // 统一资源
        URL url = new URL(actionUrl);
        // 连接类的父类，抽象类
        URLConnection urlConnection = url.openConnection();
        // http的连接类
        HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;

        // 设置是否从httpUrlConnection读入，默认情况下是true;
        httpURLConnection.setDoInput(true);
        // 设置是否向httpUrlConnection输出
        httpURLConnection.setDoOutput(true);
        // Post 请求不能使用缓存
        httpURLConnection.setUseCaches(false);
        // 设定请求的方法，默认是GET
        httpURLConnection.setRequestMethod("POST");
        // 设置字符编码连接参数
        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
        // 设置字符编码
        httpURLConnection.setRequestProperty("Charset", "UTF-8");
        // 设置请求内容类型
        httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        Optional.ofNullable(headerMap).ifPresent(stringStringMap -> {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        });

        try (DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream())) {
            for (int i = 0; i < uploadFilePaths.length; i++) {
                String uploadFile = uploadFilePaths[i];
                String filename = uploadFile.substring(uploadFile.lastIndexOf("//") + 1);
                dataOutputStream.writeBytes(twoHyphens + boundary + LINE_SEPARATOR);
                dataOutputStream.writeBytes("Content-Disposition: form-data; " + "name=\"file" + "\";filename=\"" + filename + "\"" + LINE_SEPARATOR);
                dataOutputStream.writeBytes("Content-Type: " + i + "\"" + LINE_SEPARATOR);
                dataOutputStream.writeBytes(LINE_SEPARATOR);
                try (FileInputStream fStream = new FileInputStream(uploadFile)) {
                    int bufferSize = 1024;
                    byte[] buffer = new byte[bufferSize];
                    int length;
                    while ((length = fStream.read(buffer)) != -1) {
                        dataOutputStream.write(buffer, 0, length);
                    }
                    dataOutputStream.writeBytes(LINE_SEPARATOR);
                }
            }
            dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + LINE_SEPARATOR);
            dataOutputStream.flush();

            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = httpURLConnection.getInputStream();
                     InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                     BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    String tempLine;
                    while ((tempLine = bufferedReader.readLine()) != null) {
                        stringBuilder.append(tempLine);
                        stringBuilder.append("\n");
                    }
                }
            } else {
                System.out.println(responseCode);
                if (httpURLConnection.getResponseCode() >= 300) {
                    throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return stringBuilder.toString();
    }
}
