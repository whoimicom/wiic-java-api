package kim.kin.utils;


import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


/**
 * @author kin.kim
 * @since 2023-10-12
 **/
@SuppressWarnings({"unused", "ResultOfMethodCallIgnored"})
@Component
public class FileKimUtils {

    //    @Value("${fs.apiurl}")
    //private String fsApiurl = "http://172.16.2.172:10011/huij-fs";
    private final String fsApiurl = "http://172.16.2.160:10016/oa-api/api/pictures";
    private static final FileKimUtils instance = null;

    public static FileKimUtils getInstance() {
        if (instance == null) {
            return new FileKimUtils();
        }
        return instance;
    }

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
//        String resp = getInstance().postJson("http://localhost:8080/test/test", "{\"custCmonId\":\"12345678\",\"custNo\":\"111\",\"custNo111\":\"706923\"}");
//        String resp = getInstance().getForm("http://localhost:10011/huij-fs/testpost");
//        long startTime = System.currentTimeMillis();
//
//
//        File file = new File("D:\\data\\fs\\3.jpg");
//        InputStream input = new FileInputStream(file);
//        byte[] bytes = new byte[input.available()];
//        input.read(bytes);
//        System.out.println(bytes[1]);
//        System.out.println("bytes2:" + Base64.getEncoder().encodeToString(bytes));

        //上传多文件
//        String v1 = getInstance().uploadFile(new String[]{"D:\\data\\fs\\1.png", "D:\\data\\fs\\1.1.jpg"});
//        System.out.println(v1);


//        //上传单文件

//        String v2 = getInstance().uploadFile("D:\\data\\fs\\2.png");
//        System.out.println(v2);

//        通过字节数组上传

//        String v3 = getInstance().uploadFile(bytes, "3.jpg");
//        System.out.println(v3);
//            readInputStream(new FileInputStream("D:\\application\\ludashisetup2020.exe")); // 1378
//        fileSaveAs("D:\\application\\ludashisetup2020.exe","D:\\application\\ludashisetup2020.exe1");// 1630

        String filePath = "C:\\Users\\kinkim\\Desktop\\domain";
        certFileDeal(filePath);
//        fileNameTrans("D:\\opt\\workspace\\notes", "_", "-");
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);

    }

    public static void compressZip(String sourceFilePath, String zipFilePath) {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            File sourceFile = new File(sourceFilePath);
            compress(sourceFile, zipOutputStream, sourceFile.getName(), true);
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        }
    }

    private static void compress(File sourceFile, ZipOutputStream zipOutputStream, String name, boolean KeepDirStructure) throws Exception {
        byte[] buf = new byte[1024];
        if (sourceFile.isFile()) {
            zipOutputStream.putNextEntry(new ZipEntry(name));
            int len;
            try (FileInputStream in = new FileInputStream(sourceFile)) {
                while ((len = in.read(buf)) != -1) {
                    zipOutputStream.write(buf, 0, len);
                }
                zipOutputStream.closeEntry();
            }
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                if (KeepDirStructure) {
                    zipOutputStream.putNextEntry(new ZipEntry(name + "/"));
                    zipOutputStream.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    if (KeepDirStructure) {
                        String dirName = name + "/" + file.getName();
                        compress(file, zipOutputStream, dirName, true);
                    } else {
                        compress(file, zipOutputStream, file.getName(), false);
                    }
                }
            }
        }
    }

    public static void decompressZip(String srcFilePath, String destDirPath) throws RuntimeException {
        long start = System.currentTimeMillis();
        File srcFile = new File(srcFilePath);
        if (!srcFile.exists()) {
            throw new RuntimeException(srcFile.getPath() + "所指文件不存在");
        }
        try (ZipFile zipFile = new ZipFile(srcFile)) {
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                System.out.println("解压" + entry.getName());
                if (entry.isDirectory()) {
                    String dirPath = destDirPath + "/" + entry.getName();
                    File dir = new File(dirPath);
                    dir.mkdirs();
                } else {
                    File targetFile = new File(destDirPath + "/" + entry.getName());
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();
                    try (InputStream is = zipFile.getInputStream(entry);
                         FileOutputStream fos = new FileOutputStream(targetFile)) {
                        int len;
                        byte[] buf = new byte[1024];
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                        }
                    }
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("解压完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            throw new RuntimeException("unzip error from ZipUtils", e);
        }
    }

    /**
     * 文件拷贝
     *
     * @param strOldpath strOldpath
     * @param strNewPath strNewPath
     * @throws IOException IOException
     */
    public static void fileCopy(String strOldpath, String strNewPath) throws IOException {
        File fOldFile = new File(strOldpath);
        if (fOldFile.exists()) {
            int bytesum = 0;
            int byteread;
            InputStream inputStream = new FileInputStream(fOldFile);
            FileOutputStream fileOutputStream = new FileOutputStream(strNewPath);
            byte[] buffer = new byte[1444];
            while ((byteread = inputStream.read(buffer)) != -1) {
                bytesum += byteread;
                fileOutputStream.write(buffer, 0, byteread);
            }
            inputStream.close();
            fileOutputStream.close();
        }
    }

    public static void fileSaveAs(String strOldpath, String strNewPath) throws IOException {
        Files.move(new File(strOldpath).toPath(), new File(strNewPath).toPath());
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        return bytes;
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
                        deleteFolder(file);
                    } else {
                        file.delete();
                    }
                }
            }
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
     * 使用FileChannel从文件复制到文件
     * 大文件可用此方法加速
     */
    public static void copyFileUsingFileChannels(File source, File dest)
            throws IOException {
        try (FileChannel inputChannel = new FileInputStream(source).getChannel()) {
            try (FileChannel outputChannel = new FileOutputStream(dest).getChannel()) {
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            }
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
     * @return httpUrl
     * @throws Exception Exception
     */
    public String uploadFile(String[] files) throws Exception {
        return getInstance().uploadFile(files, null);
    }


    /**
     * 多文件路径上传
     *
     * @param files     files
     * @param headerMap headerMap
     * @return url
     * @throws Exception Exception
     */
    public String uploadFile(String[] files, Map<String, String> headerMap) throws Exception {
        return HttpKimUtils.getInstance().uploadMultipartFile(fsApiurl + "/uploads", files, headerMap);
    }

    /**
     * 多文件路径上传
     *
     * @param files     files
     * @param headerMap headerMap
     * @return url
     * @throws Exception Exception
     */
    public String uploadFile(String url, String[] files, Map<String, String> headerMap) throws Exception {
        return HttpKimUtils.getInstance().uploadMultipartFile(url, files, headerMap);
    }

    /**
     * 单文件路径上传
     *
     * @param filePath  filePath
     * @param headerMap headerMap
     * @return url
     * @throws Exception Exception
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
     * @param bytes            bytes
     * @param originalFilename originalFilename
     * @param headerMap        headerMap
     * @return url
     * @throws Exception Exception
     */
    public String uploadFile(byte[] bytes, String originalFilename, Map<String, String> headerMap) throws Exception {
        return HttpKimUtils.getInstance().postFile(fsApiurl + "/uploadbytes", bytes, originalFilename, headerMap);
    }

    /**
     * 阿里证书批处理工具，解压证书，规范命名
     *
     * @param fileDir 证书目录
     * @throws IOException ioE
     */
    public static void certFileDeal(String fileDir) throws IOException {
        String certsPath = fileDir + File.separator + "certs";
        Path path = Paths.get(certsPath);
        File filePath = new File(certsPath);
        if (!filePath.exists()) {
            Files.createDirectory(path);
        }
        File dirFile = new File(fileDir);
        File[] zipFiles = dirFile.listFiles((dir, name) -> name.endsWith(".zip"));
        assert zipFiles != null;
        Arrays.stream(zipFiles).forEach(file -> {
            decompressZip(file.getAbsolutePath(), fileDir);
        });
        File[] certFiles = dirFile.listFiles((dir, name) -> !name.endsWith(".zip") && !name.contains("certs"));
        assert certFiles != null;
        for (File file : certFiles) {
            String name = file.getName();
            int i = name.indexOf("_");
            Files.move(file.toPath(), Paths.get(certsPath, name.substring(i + 1)),StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * 替换文件中包含的字符串
     *
     * @param fileDir    fileDir
     * @param sourceChar sourceChar
     * @param tarChar    tarChar
     * @throws IOException IOException
     */
    public static void fileNameTrans(String fileDir, String sourceChar, String tarChar) throws IOException {
        if (new File(fileDir).exists()) {
            Files.walkFileTree(Paths.get(fileDir), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                    String fileName = path.getFileName().toString();
                    String previousPath = path.getParent().toFile().getPath();
                    String currentPath = path.toFile().getPath();
                    if (fileName.contains(sourceChar)) {
                        String newName = fileName.replaceAll(sourceChar, tarChar);
                        System.out.println(fileName + " " + newName + " " + previousPath + " " + currentPath);
                        Files.move(path, Paths.get(previousPath, newName));
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
                    return super.preVisitDirectory(path, attrs);
                }
            });
        }
    }


}
