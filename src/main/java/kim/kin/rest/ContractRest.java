package kim.kin.rest;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import kim.kin.config.security.AnonymousKimAccess;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author choky
 */
@Controller
public class ContractRest {

    @GetMapping("/contract/{id}")
    @AnonymousKimAccess
    public String contract(@PathVariable String id, Model model) {
        record Loan(String xtSn,String repaymentAccount, String channel, String repaymentName, String applySn) {
        }
        record MemberClientele(String creSn,String censusAddrDetail) {

        }
        record Member(String mobile,String email,String gender, String creSn, String realName, Loan loan, Map<String, String> paramMap,
                      MemberClientele memberClientele) {
        }
        record RepaymentDates(String period, String repaymenDate, String repaymenMoney) {

        }
        record FundsSource(String companyName) {

        }

        RepaymentDates repaymentDate1 = new RepaymentDates("1", "20230101", "550.00");
        RepaymentDates repaymentDate2 = new RepaymentDates("2", "20230201", "551.00");
        RepaymentDates repaymentDate3 = new RepaymentDates("3", "20230301", "552.00");
        List<RepaymentDates> repaymentDates = Arrays.asList(repaymentDate1, repaymentDate2, repaymentDate3);
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("repaymentBank", "工商银行");
        paramMap.put("censusProvince", "重庆");
        paramMap.put("censusCity", "重庆市");
        paramMap.put("censusDistrict", "渝北区");
        String channel = "08";
        Loan loan = new Loan("HT22256465********","6226***********", channel, "工商银行", "90SDFKSLDFKJ***");
        MemberClientele memberClientele = new MemberClientele("XXXXCRESN","金山商业中心");
        Member member = new Member("18555555555","@5dhj.com","1", "3333333CRESN", "麻子", loan, paramMap, memberClientele);

        model.addAttribute("member", member);
        model.addAttribute("repaymentDates", repaymentDates);
        model.addAttribute("fundsSource", new FundsSource("hoben-api"));
        String currentDate = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        model.addAttribute("currDate", currentDate);
        model.addAttribute("currYear", currentDate.substring(0, 4));
        model.addAttribute("currMonth", currentDate.substring(4, 6));
        model.addAttribute("currDay", currentDate.substring(6, 8));
//        return "pdf_template12403.html";
        return "pdf_template" + id + ".html";
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
            builder.withUri("http://localhost:1987/kim-api/contract/12404");
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
