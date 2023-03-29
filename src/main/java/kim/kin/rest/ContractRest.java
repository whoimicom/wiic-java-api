package kim.kin.rest;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import kim.kin.config.security.AnonymousKimAccess;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

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

    /**
     * 查看模板
     * <a href="http://localhost:1987/kim-api/contract/12405">...</a>
     * @param id 模板编号
     * @param model model
     * @return 模板
     */
    @GetMapping("/contract/{id}")
    @AnonymousKimAccess
    public String contract(@PathVariable String id, Model model) {
        record Loan(String downDate, String perMonthDate, String periods, String loanAmt, String borrowReason,
                    String xtSn, String repaymentAccount, String channel, String repaymentName, String applySn,
                    String fundsSn) {
        }
        record MemberClientele(String creSn, String censusAddrDetail, String bodies, String creValid,
                               String companyName, String companyAddrDetail, String liveAddrDetail) {

        }
        record Member(String applyTime,String qq, String nationality, String birthday, String mobile, String email, String gender,
                      String creSn, String realName, Loan loan, Map<String, String> paramMap,
                      MemberClientele memberClientele) {
        }
        record RepaymentDates(String period, String repaymenDate, String planPrincipal, String planServiceFee,
                              String repaymenMoney) {
        }
        record FundsSource(String companyName, String companyLogo) {

        }
        record ContactothersHb(String parentsName, String family1Name, String contactName, String parentsTel,
                               String family1Tel, String contactTel, String parentsRel, String family1Relation,
                               String contactRel) {
        }
        String currentDate = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("repaymentBank", "工商银行");
        paramMap.put("gender", "女");
        paramMap.put("companyProvince", "重庆市");
        paramMap.put("censusProvince", "重庆市");
        paramMap.put("liveProvince", "重庆市");
        paramMap.put("companyCity", "重庆");
        paramMap.put("censusCity", "重庆");
        paramMap.put("liveCity", "重庆");
        paramMap.put("companyDistrict", "渝北");
        paramMap.put("censusDistrict", "渝北");
        paramMap.put("liveDistrict", "渝北");
        paramMap.put("companyAddrDetail", "**路**街道**号");
        paramMap.put("censusAddrDetail", "**路**街道**号");
        paramMap.put("liveAddrDetail", "**路**街道**号");
        String channel = "08";

        RepaymentDates repaymentDate1 = new RepaymentDates("1", "20230101", "5000", "100", "550.00");
        RepaymentDates repaymentDate2 = new RepaymentDates("2", "20230201", "5000", "100", "551.00");
        RepaymentDates repaymentDate3 = new RepaymentDates("3", "20230301", "5000", "100", "552.00");
        List<RepaymentDates> repaymentDates = Arrays.asList(repaymentDate1, repaymentDate2, repaymentDate3);

        Loan loan = new Loan("20230101", "1日", "18", "5000", "购买XXXX", "HT22256465********", "6226***********", channel, "麻子", "APPLYSNXXX***", "131");
        MemberClientele memberClientele = new MemberClientele("XXXXCRESN", "金山商业中心", "渝北", "20550101", "**公司", "**路**街道**号", "**路**街道**号");
        Member member = new Member( currentDate, "QQ*****", "汉", "20230101", "18555555555", "@5dhj.com", "1", "3333333CRESN", "麻子", loan, paramMap, memberClientele);
        FundsSource fundsSource = new FundsSource("hoben-api", "https://hw.5dhj.com/huij-fs/file/common/app/images/logo_10_tz.png");
        ContactothersHb contactothersHb = new ContactothersHb("麻大", "麻三", "麻烦", "185*******", "185*******", "185*******", "父子", "母子", "朋友 ");
        model.addAttribute("member", member);
        model.addAttribute("repaymentDates", repaymentDates);
        model.addAttribute("monthRat", "0.15%");
        model.addAttribute("fundsSource", fundsSource);

        model.addAttribute("currDate", currentDate);
        model.addAttribute("currYear", currentDate.substring(0, 4));
        model.addAttribute("currMonth", currentDate.substring(4, 6));
        model.addAttribute("currDay", currentDate.substring(6, 8));
        model.addAttribute("contactothersHb", contactothersHb);
        model.addAttribute("srviceRat", "3%");
        model.addAttribute("yearRat", "18%");
        model.addAttribute("LoanAmtUp", "伍仟");
        model.addAttribute("latDate", "20230301");
        model.addAttribute("name", "麻三");
        model.addAttribute("creSn", "XXXXCRESN");
        model.addAttribute("bankCardNo", "6226***********");
        model.addAttribute("bankDesc", "工行银行");
//        return "pdf_template12403.html";
        String temp = "pdf_template" + id + ".html";
        System.out.println(temp);
        return temp;
    }

    /**
     * 生成根据模板生成PDF
     * <a href="http://localhost:1987/kim-api/genpdf/12405">...</a>
     * @param id 模板编号
     * @return 路径
     */
    @GetMapping("/gen/{id}")
    @AnonymousKimAccess
    @ResponseBody
    public String genpdf(@PathVariable String id) {
        int nano = LocalDateTime.now().getSecond();
        try (OutputStream os = new FileOutputStream("d:\\contract.pdf")) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            ClassPathResource classPathResource = new ClassPathResource("fonts/SimSun.ttf");
//            InputStream inputStream = classPathResource.getInputStream();
            builder.useFont(classPathResource.getFile(), "SimSun");
            builder.useFastMode();
            builder.withUri("http://localhost:1987/kim-api/contract/" + id);
//            builder.withHtmlContent(Files.readString(Paths.get("d:\\pdf.html")), "d:\\pdf.html");
            builder.toStream(os);
            builder.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(LocalDateTime.now().getSecond() - nano);
        return "d:\\contract.pdf  pdf_template" + id + ".html";
    }
}
