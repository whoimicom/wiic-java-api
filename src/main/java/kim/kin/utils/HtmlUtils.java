package kim.kin.utils;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author kin.kim
 * @since 2024-06-07
 **/
@Component
public class HtmlUtils {
    private static final Logger log = LoggerFactory.getLogger(HtmlUtils.class);
    @Value("${fonts.font-family}")
    private String fontFamily;
    @Value("${kim.kin.file-path}")
    private String tempPath;
    @Value("${spring.thymeleaf.suffix}")
    private String suffix;
    @Resource
    private TemplateEngine templateEngine;

    /**
     * 根据模板和参数生成本地HTML
     *
     * @param templateName 模板名
     * @param fileName     文件名
     * @param paramMap     参数
     * @return filePath
     */
    public String genLocalFile(String templateName, String fileName, Map<String, Object> paramMap) throws IOException {
        paramMap.putIfAbsent("fontFamily", fontFamily);
        Context context = new Context();
        context.setVariables(paramMap);
        String filePath = tempPath + File.separator + fileName;
        File folder = new File(filePath);
        try (PrintWriter writer = new PrintWriter(folder, StandardCharsets.UTF_8)) {
            templateEngine.process(templateName, context, writer);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        return filePath;
    }
}
