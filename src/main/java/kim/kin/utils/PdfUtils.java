package kim.kin.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.core.io.ClassPathResource;

public class PdfUtils {
    public static void main(String[] args) throws Exception {
        try (OutputStream os = new FileOutputStream("d:\\out.pdf")) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            ClassPathResource classPathResource = new ClassPathResource("fonts/SimSun.ttf");
//            InputStream inputStream = classPathResource.getInputStream();
            builder.useFont(classPathResource.getFile(), "SimSun");
//            builder.withUri("https://zditect.com/main-advanced/java/java-html-to-pdf.html");
            builder.withHtmlContent(Files.readString(Paths.get("d:\\pdf.html")), "d:\\pdf.html");
            builder.toStream(os);
            builder.run();
        }
    }
}