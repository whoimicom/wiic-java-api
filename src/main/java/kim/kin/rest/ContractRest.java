package kim.kin.rest;

import kim.kin.config.security.handler.AnonymousKimAccess;
import kim.kin.utils.PdfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 合同生成
 *
 * @author kinkim
 */
@Controller
@RequestMapping("/contract")
public class ContractRest {

    @Value("${fonts.font-family}")
    private String fontFamily;

    private PdfUtils pdfUtils;

    @Autowired
    public void setPdfUtils(PdfUtils pdfUtils) {
        this.pdfUtils = pdfUtils;
    }

    /**
     * 查看模板
     * localhost:1987/kim-api/contract/preview/ApplicationForm
     *
     * @param tempName 模板名称
     * @param model    model
     * @return 模板
     */
    @GetMapping("/preview/{tempName}")
    @AnonymousKimAccess
    public String preview(@PathVariable String tempName, Model model) {
        LocalDate now = LocalDate.now();
        String sixMouthsLater = now.plusMonths(6).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String currentDate = now.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String yyyy = currentDate.substring(0, 4);
        String mm = currentDate.substring(5, 7);
        String dd = currentDate.substring(8, 10);
        String idNo="51160020220222******";
        String birthday=idNo.substring(6,14);
        //客户信息
        record Customer(String name, String gender, String birthday, String tel, String qq, String email,
                        String idNo, String idProvince, String idCity,
                        String idDistrict, String idAddrDetail, String idIssuePlace, String idValidity,
                        String liveProvince, String liveCity, String liveDistrict, String liveAddrDetail,
                        String nation, String companyName, String companyProvince,
                        String companyCity, String companyDistrict, String companyAddrDetail) {
        }
        Customer customer = new Customer("KINKIM", "M", birthday, "185****0088", "198799**", "im@kin.kim",
                idNo, "**省", "**市", "**县", "**路**街道**号",
                "**县", "2099-12-31", "**省", "**市", "**区", "**路**街道**号",
                "汉", "非自然现象研究院", "重庆", "重庆", "**北", "**路**街道**号");
        //合同信息
        record Contact(String name, String tel, String relation) {
        }
        List<Contact> contacts = Arrays.asList(
                new Contact("A", "177****7777", "A关系"),
                new Contact("B", "199****9999", "B关系"),
                new Contact("C", "133****3333", "C关系")
        );
        //贷款信息
        record Loan(String id, String amount, String amountUp, String periods, String purpose, String monthRate,
                    String repaymentMethod, String firstPayDate, String perPayDate, String repaymentName,
                    String repaymentAccount,
                    String repaymentBank, String contractDate, String expirationDate) {
        }
        Loan loan = new Loan("LOAN" + currentDate, "6000", "六千元整", "6", "消费", "0.35%",
                "等额本息", plusMonths(now, 1), dd, "KINKIM", "2423450923845",
                "平安银行", currentDate, sixMouthsLater);
        // 资方信息
        record Capital(String name, String shortName, String logoAddr, String bankName, String bankAccount,
                       String legalRepresentative, String registrationAddr, String tel, String contactAddr) {
        }
        Capital capital = new Capital("XX融资担保有限公司", "担保公司", "http://localhost:1987/kim-api/loan.png",
                "XX银行", "33333333****333333", "XX法人", "XX省XX市", "0826-*****", "XX省XX市");
        record Repayment(String stages, String date, String principal, String serviceFee, String monthlyPayment) {
        }
        //还款计划
        List<Repayment> repayments = Arrays.asList(
                new Repayment("1", currentDate, "500", "50", "550"),
                new Repayment("2", plusMonths(now, 1), "500", "50", "550"),
                new Repayment("3", plusMonths(now, 2), "500", "50", "550"),
                new Repayment("4", plusMonths(now, 3), "500", "50", "550"),
                new Repayment("5", plusMonths(now, 4), "500", "50", "550"),
                new Repayment("6", plusMonths(now, 5), "500", "50", "550")
        );

        model.addAttribute("customer", customer);
        model.addAttribute("contacts", contacts);
        model.addAttribute("loan", loan);
        model.addAttribute("repayments", repayments);
        model.addAttribute("capital", capital);

        model.addAttribute("currentDate", currentDate);
        model.addAttribute("contractYear", yyyy);
        model.addAttribute("contractMonth", mm);
        model.addAttribute("contractDay", dd);

        model.addAttribute("xtSn", "T" + currentDate);
        model.addAttribute("customerHotline", "100861001010000");

        model.addAttribute("fontFamily", fontFamily);
        String temp = "TEMP-" + tempName + ".html";
        System.out.println(temp);
        return temp;
    }

    private String plusMonths(LocalDate localDate, long plusMouth) {
        return localDate.plusMonths(plusMouth).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * 生成根据模板生成PDF
     * localhost:1987/kim-api/contract/generate/ApplicationForm
     *
     * @param tempName 模板名称
     * @return 路径
     */
    @GetMapping("/generate/{tempName}")
    @AnonymousKimAccess
    @ResponseBody
    public String generate(@PathVariable String tempName) throws IOException {
        String uri = "http://localhost:1987/kim-api/contract/preview/" + tempName;
        int nextInt = new Random().nextInt();
        String outputPath = "d:\\contract\\" + tempName + "-" + fontFamily + "-" + nextInt + ".pdf";
        pdfUtils.generatePdf(uri, outputPath);
        return outputPath + "  TEMP-" + tempName + ".html";
    }

    /**
     * localhost:1987/kim-api/contract/generateAll
     */
    @GetMapping("/generateAll")
    @AnonymousKimAccess
    @ResponseBody
    public void generateAll() {
        List<String> list = Arrays.asList("ApplicationForm", "AuthorizationLetter", "AuthorizationLetter-Credit",
                "AuthorizationLetter-Person", "CertificateOfIndebtedness", "ConfirmationLetter", "DelegationDeduction",
                "LetterOfAssignment", "LoanContract", "RepaymentGuide", "ServiceContract");
        Random random = new Random();
        int nextInt = random.nextInt();
//        int size = Math.abs(nextInt % list.size());
//        String tempName = list.get(size);
        list.forEach(tempName -> {
            String uri = "http://localhost:1987/kim-api/contract/preview/" + tempName;
            String outputPath = "d:\\contract\\" + tempName + "-" + fontFamily + "-" + nextInt + ".pdf";
            try (OutputStream ignored = new FileOutputStream(outputPath)) {
                pdfUtils.generatePdf(uri, outputPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
