package kim.kin.utils;


import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


/**
 * @author kin.kim
 */
@Component
public class FileKimUtils {

    //    @Value("${fs.apiurl}")
    //private String fsApiurl = "http://172.16.2.172:10011/huij-fs";
    private final String fsApiurl = "http://172.16.2.160:10016/oa-api/api/pictures";
    private static FileKimUtils instance = null;

    public static FileKimUtils getInstance() {
        if (instance == null) {
            return new FileKimUtils();
        }
        return instance;
    }

    public static void main(String[] args) throws Exception {

//        String resp = getInstance().postJson("http://localhost:8080/test/test", "{\"custCmonId\":\"12345678\",\"custNo\":\"111\",\"custNo111\":\"706923\"}");
//        String resp = getInstance().getForm("http://localhost:10011/huij-fs/testpost");
        long startTime = System.currentTimeMillis();


        File file = new File("D:\\data\\fs\\3.jpg");
        InputStream input = new FileInputStream(file);
        byte[] bytes = new byte[input.available()];
        input.read(bytes);
        System.out.println(bytes[1]);
        System.out.println("bytes2:" + Base64.getEncoder().encodeToString(bytes));

        //上传多文件
//        String v1 = getInstance().uploadFile(new String[]{"D:\\data\\fs\\1.png", "D:\\data\\fs\\1.1.jpg"});
//        System.out.println(v1);


//        //上传单文件

//        String v2 = getInstance().uploadFile("D:\\data\\fs\\2.png");
//        System.out.println(v2);

//        通过字节数组上传

        String v3 = getInstance().uploadFile(bytes, "3.jpg");
        System.out.println(v3);

    }

    public static void deleteFolder(File folder) throws Exception {
        if (!folder.exists()) {
            throw new Exception("文件不存在");
        }
        if (!folder.isFile() && folder.list() != null) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        //递归直到目录下没有文件
                        deleteFolder(file);
                    } else {
                        //删除
                        file.delete();
                    }
                }
            }
            //删除
        }
        folder.delete();
    }

    /**
     * 从文件复制到文件
     * StandardCopyOption.REPLACE_EXISTING 如果文件存在则覆盖
     */
    public static void copyFileUsingFiles(File source, File dest)
            throws IOException {
        Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 从输入流复制到文件
     * StandardCopyOption.REPLACE_EXISTING 如果文件存在则覆盖
     */
    public static void copyFileUsingFiles(InputStream in, File dest)
            throws IOException {
        Files.copy(in, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 从文件复制到输出流
     */
    public static void copyFileUsingFiles(File source, OutputStream out)
            throws IOException {
        Files.copy(source.toPath(), out);
    }

    /**
     * 使用FileChannel从文件复制到文件 据说此方法要快些
     * 大文件可用此方法
     */
    public static void copyFileUsingFileChannels(File source, File dest)
            throws IOException {
        try (FileChannel inputChannel = new FileInputStream(source).getChannel();
             FileChannel outputChannel = new FileOutputStream(dest).getChannel()) {
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }
    }

    /**
     * 文件下载
     *
     * @param httpUrl     下载地址
     * @param saveFileUrl 保存地址
     * @return map.status-0失败，1成功
     */
    public static Map<String, String> downLoadFile(String httpUrl, String saveFileUrl) {
        Map<String, String> result = new HashMap<>(5);
        if (null == httpUrl || "".equals(httpUrl.trim())) {
            result.put("status", "0");
            result.put("msg", "下载地址httpUrl参数为空");
            return result;
        }
        if (null == saveFileUrl || "".equals(saveFileUrl.trim())) {
            result.put("status", "0");
            result.put("msg", "保存文件地址saveFileUrl参数为空");
            return result;
        }
        if (httpUrl.equals(saveFileUrl)) {
            result.put("status", "0");
            result.put("msg", "httpUrl与saveFileUrl地址相同");
            return result;
        }
        try {
            URL source = new URL(httpUrl);
            try (InputStream input = source.openStream()) {
                try {
                    copyFileUsingFiles(input, new File(saveFileUrl));
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                    if (!"Premature EOF".equals(ex.getMessage())) {//Premature EOF这个异常可以不用管
                        ex.printStackTrace();
                        result.put("status", "0");
                        result.put("msg", "下载出错" + ex.getMessage());
                        return result;
                    }
                }
            }
            result.put("status", "1");
            result.put("msg", "下载成功");
            return result;
        } catch (Exception e) {
            result.put("status", "0");
            result.put("msg", "下载出错" + e.getMessage());
            return result;
        }
    }


    public String uploadFile(byte[] bytes, String originalFilename) throws Exception {
        return getInstance().uploadFile(bytes, originalFilename, null);

    }

    public String uploadFile(String filePath) throws Exception {

        return getInstance().uploadFile(filePath, null);

    }

    /**
     * @param files 多文件路径
     * @return
     * @throws Exception
     */
    public String uploadFile(String[] files) throws Exception {
        return getInstance().uploadFile(files, null);
    }


    /**
     * 多文件路径上传
     *
     * @param files
     * @param headerMap
     * @return
     * @throws Exception
     */
    public String uploadFile(String[] files, Map<String, String> headerMap) throws Exception {
        return HttpKimUtils.getInstance().uploadMultipartFile(fsApiurl + "/uploads", files, headerMap);
    }

    /**
     * 多文件路径上传
     *
     * @param files
     * @param headerMap
     * @return
     * @throws Exception
     */
    public String uploadFile(String url, String[] files, Map<String, String> headerMap) throws Exception {
        return HttpKimUtils.getInstance().uploadMultipartFile(url, files, headerMap);
    }

    /**
     * 单文件路径上传
     *
     * @param filePath
     * @param headerMap
     * @return
     * @throws IOException
     */
    public String uploadFile(String filePath, Map<String, String> headerMap) throws Exception {
        return HttpKimUtils.getInstance().postFile(fsApiurl + "/uploadbytes", filePath, headerMap);
    }

    public String uploadFile(String url, String filePath, Map<String, String> headerMap) throws Exception {
        return HttpKimUtils.getInstance().postFile(url, filePath, headerMap);
    }

    /**
     * 文件字节上传
     *
     * @param bytes
     * @param originalFilename
     * @param headerMap
     * @return
     * @throws IOException
     */
    public String uploadFile(byte[] bytes, String originalFilename, Map<String, String> headerMap) throws Exception {
        return HttpKimUtils.getInstance().postFile(fsApiurl + "/uploadbytes", bytes, originalFilename, headerMap);
    }




}
