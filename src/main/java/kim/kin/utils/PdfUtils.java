package kim.kin.utils;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class PdfUtils {

    public void generatePdf(String htmlUrl, String outputPath) throws IOException {
        try (OutputStream os = new FileOutputStream(outputPath)) {
            PdfRendererBuilder pdfRendererBuilder = new PdfRendererBuilder();
            String fontPath = System.getProperty("user.home") + "/SimSun.ttf";
            Path path = Paths.get(fontPath);
            if (!Files.exists(path)) {
                ClassPathResource classPathResource = new ClassPathResource("fonts/SimSun.ttf");
                try (InputStream inputStream = classPathResource.getInputStream()) {
                    Files.copy(inputStream, path);
                }
            }
            pdfRendererBuilder.useFont(new File(fontPath), "SimSun");
            pdfRendererBuilder.useFastMode();
            pdfRendererBuilder.withUri(htmlUrl);
            pdfRendererBuilder.toStream(os);
            pdfRendererBuilder.run();
        }
    }

    public static void main(String[] args) throws Exception {
        try (OutputStream os = new FileOutputStream("d:\\out.pdf")) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            String userHome = System.getProperty("user.home");
            String fontPath = userHome + "/SimSun.ttf";
            Path path = Paths.get(fontPath);
            if (!Files.exists(path)) {
                ClassPathResource classPathResource = new ClassPathResource("fonts/SimSun.ttf");
                try (InputStream inputStream = classPathResource.getInputStream()) {
                    Files.copy(inputStream, path);
                }
            }

            builder.useFont(new File(fontPath), "SimSun");
            File file = ResourceUtils.getFile("classpath:fonts/SimSun.ttf");

            builder.useFont(file, "SimSun");

//            builder.withUri("https://zditect.com/main-advanced/java/java-html-to-pdf.html");
            builder.withHtmlContent(Files.readString(Paths.get("d:\\pdf.html")), "d:\\pdf.html");
            builder.toStream(os);
            builder.run();
        }
    }
}