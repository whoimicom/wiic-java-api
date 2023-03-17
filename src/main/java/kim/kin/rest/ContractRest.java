package kim.kin.rest;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import kim.kin.config.security.AnonymousKimAccess;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author choky
 */
@Controller
public class ContractRest {

    @GetMapping("/contract")
    @AnonymousKimAccess
    public String contract(Model model) {
//            <strong>身份证号码：<u> th:text="${member.creSn}" </u></strong><br/>
//    <strong>授权扣款银行账户：<u> ${member.loan.repaymentAccount} </u></strong><br/>
//        鉴于授权人于<u> ${currYear} </u>年<u> ${currMonth} </u>月<u> ${currDay}
//    <strong>签署日期：${currDate}</strong><br/>
        record Loan(String repaymentAccount) {
        }
        record Member(String creSn, Loan loan, String realName) {
        }

        model.addAttribute("member", new Member("creSn---", new Loan("rem"), "realName"));
        String currentDate = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        model.addAttribute("currDate", currentDate);
        model.addAttribute("currYear", currentDate.substring(0,4));
        model.addAttribute("currMonth", currentDate.substring(4,6));
        model.addAttribute("currDay", currentDate.substring(6,8));
        return "pdf_template12403.html";
    }

    @GetMapping("/gen")
    @AnonymousKimAccess
    @ResponseBody
    public String gen(Model model) {
        int nano = LocalDateTime.now().getSecond();
        try (OutputStream os = new FileOutputStream("d:\\contract.pdf")) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            ClassPathResource classPathResource = new ClassPathResource("fonts/SimSun.ttf");
//            InputStream inputStream = classPathResource.getInputStream();
            builder.useFont(classPathResource.getFile(), "SimSun");
            builder.useFastMode();
            builder.withUri("http://localhost:1987/kim-api/contract");
//            builder.withHtmlContent(Files.readString(Paths.get("d:\\pdf.html")), "d:\\pdf.html");
            builder.toStream(os);
            builder.run();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(LocalDateTime.now().getSecond() - nano);
        return "pdf_template12403.html";
    }
}
