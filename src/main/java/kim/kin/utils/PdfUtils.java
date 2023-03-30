package kim.kin.utils;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PdfUtils {
    public static void main(String[] args) throws Exception {
        try (OutputStream os = new FileOutputStream("d:\\out.pdf")) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            String userHome = System.getProperty("user.home");
            String fontPath = userHome + "/SimSun.ttf";
            Path path = Paths.get(fontPath);
            if (!Files.exists(path)) {
                ClassPathResource classPathResource = new ClassPathResource("fonts/SimSun.ttf");
                InputStream inputStream = classPathResource.getInputStream();
                Files.copy(inputStream, path);
            }

            builder.useFont(new File(fontPath), "SimSun");
//            builder.useFont(ResourceUtils.getFile("classpath:fonts/SimSun.ttf"), "SimSun");

//            builder.withUri("https://zditect.com/main-advanced/java/java-html-to-pdf.html");
            builder.withHtmlContent(Files.readString(Paths.get("d:\\pdf.html")), "d:\\pdf.html");
            builder.toStream(os);
            builder.run();
        }
    }
}