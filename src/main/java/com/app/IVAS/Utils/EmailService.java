package com.app.IVAS.Utils;



import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;


/**
 * Created by Buchi on 26/10/2022.
 */

@Slf4j
@Data
@Service
@EnableAsync
public class EmailService {

//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Value("${spring.mail.host}")
//    private String smtpHost;
//
//    @Value("${spring.mail.port}")
//    private String smtpPort;
//
//    @Value("${spring.mail.username}")
//    private String sender;
//
//    @Value("${spring.mail.password}")
//    private String password;
//
//    @Value("${senderEmail}")
//    private String senderEmail;
//
//    @Value("${com.oasis.image.directory}")
//    private String storageDirectoryPath;
//
//    private static final Logger logger = Logger.getLogger(EmailService.class.getName());
//
//    private void buildEmailMessage(String subject, String to, String s) throws MessagingException {
//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
//        mimeMessage.setContent(s, "text/html");
//        helper.setTo(to);
//        helper.setSubject(subject);
//        helper.setFrom(senderEmail);
//        mailSender.send(mimeMessage);
//    }
//
//
//    @Async
//    public void sendMySimpleEmail(String subject, String content, String to, String tax, String asin, String name, String address, String number, String email, String rn, String rc, String amount, String category, String invoice, String year, LocalDateTime date, String ra) throws MessagingException {
//        buildEmailMessage(subject, to, setNoticeTemplate(content, tax, asin, name, address, number, email, rn, rc, amount, category, invoice, year, date, ra));
//    }
//
//    private String setNoticeTemplate(String content, String tax, String asin, String name, String address, String number, String email, String rn, String rc, String amount, String category, String invoice, String year, LocalDateTime date, String ra) {
//        return "        <div style=\"background:#f2f2f2;width:70%; border:3px solid green\">\n" +
//                "            <div>\n" +
//                "                <div style=\"padding:10px; position: relative; top: -80px;left: 120px \">\n" +
//                "                    <p style=\"color:darkgreen; font-size:18px; text-align:left; padding: 0; margin:0\">ANAMBRA STATE GOVERNMENT</p>\n" +
//                "                    <p style=\"color:darkgreen; font-size:14px; text-align:left; padding: 0; margin:0\">ANAMBRA STATE MINISTRY OF COMMERCE AND INDUSTRY</p>\n" +
//                "                    <p style=\"color:darkgreen;text-align:left; padding: 0; margin:0\">"+ra+"</p>\n" +
//                "                </div>\n" +
//                "            </div>\n" +
//                "            <div style=\" position: relative; top: -70px;background:snow; text-align:center; font-size: 12px;\">\n" +
//                "                <p style=\"padding:10px; font-size: 20px; color:darkgreen;\">DEMAND NOTICE - " + tax + " <b th:text=\"${#strings.toUpperCase(year)}\"></b></p>\n" +
//                "            </div>\n" +
//                "            <div style=\"height: 100px;\">\n" +
//                "                <div style=\"width:50%; float: right;font-size: 12px;\">\n" +
//                "                    <span style=\"display:block\" >INVOICE NUMBER: <b>"+ invoice +"</b></span>\n" +
//                "                    <span style=\"display:block\">PID: N/A</span>\n" +
//                "                    <span style=\"display:block\">INVOICE YEAR: <b>"+year+"</b></span>\n" +
//                "                    <span style=\"display:block\">DATE: <b>"+date+"</b></span>\n" +
//                "                </div>\n" +
//                "            </div>\n" +
//                "            <div style=\"position:relative; font-size: 12px;top: -120px\">\n" +
//                "                <div  style=\"float: left;border: 2px solid darkgreen; width: 37%\">\n" +
//                "                    <div  style=\"border-bottom: 2px solid darkgreen; padding:20px;\">\n" +
//                "<!--                        <p style=\"padding:0; margin:0\">Billed To  ASIN:"+asin+"</p>-->\n" +
//                "                        <p style=\"padding:0; margin:0\">Billed To:  <span>"+name+"</span></p>\n" +
//                "                        <p style=\"padding:0; margin:0\">Address: <br><span>"+address+"</span></p>\n" +
//                "                        <table style=\"width:100%\">\n" +
//                "                            <tr>\n" +
//                "                                <th style=\"text-align:left; font-size:10px\">Phone No.</th>\n" +
//                "                                <th style=\"text-align:left; font-size:10px\">E-mail</th>\n" +
//                "                            </tr>\n" +
//                "                            <tr>\n" +
//                "                                <td style=\"text-align:left; font-size:10px\">"+number+"</td>\n" +
//                "                                <td style=\"text-align:left; font-size:10px\">"+email+"</td>\n" +
//                "                            </tr>\n" +
//                "                        </table>\n" +
//                "                    </div>\n" +
//                "                    <p style=\" padding:20px;\">Nature of Business/ Residence: <br> <span>"+category+"</span></p>\n" +
//                "                </div>\n" +
//                "                <div style=\"padding:10px; font-size: 12px; width: 98%; \">\n" +
//                "                    <table style=\"border: 1px solid;float: right; width:60%\">\n" +
//                "                        <thead>\n" +
//                "                        <tr style=\"background:gray; padding: 14px;\">\n" +
//                "                            <th style=\"background:gray; padding: 4px;\">S/N</th>\n" +
//                "                            <th style=\"width:55%\">Revenue Name </th>\n" +
//                "                            <th  style=\"width:25%\">Revenue Code</th>\n" +
//                "                            <th style=\"width: 15%\">Amount (N)</th>\n" +
//                "                        </tr>\n" +
//                "                        </thead>\n" +
//                "                        <tbody>\n" +
//                "                        <tr>\n" +
//                "                            <td>1</td>\n" +
//                "                            <td style=\"border-left: 1px solid;\">"+rn+"</td>\n" +
//                "                            <td style=\"border-left: 1px solid;\">"+rc+"</td>\n" +
//                "                            <td style=\"border-left: 1px solid;\"></td>\n" +
//                "                        </tr>\n" +
//                "                        <tr>\n" +
//                "                            <td colspan=\"3\" style=\"text-align:right !important; border:1px solid\">Outstanding (2021)</td>\n" +
//                "                            <td style=\"border:1px solid\">"+amount+"</td>\n" +
//                "                        </tr>\n" +
//                "                        <tr>\n" +
//                "                            <td colspan=\"3\" style=\"text-align:right !important; border:1px solid\">Discount</td>\n" +
//                "                            <td style=\"border:1px solid\">0.0</td>\n" +
//                "                        </tr>\n" +
//                "                        <tr>\n" +
//                "                            <td colspan=\"3\" style=\"text-align:right !important; border:1px solid\">Total Due</td>\n" +
//                "                            <td style=\"border:1px solid\">"+amount+"</td>\n" +
//                "                        </tr>\n" +
//                "                        </tbody>\n" +
//                "                    </table>\n" +
//                "                </div>\n" +
//                "            </div>\n" +
//                "            <div style=\"position:relative; margin-top: 20px;\">\n" +
//                "                <div style=\"padding:30px;font-size: 12px;\">\n" +
//                "                    <p>HOW TO PAY:</p>\n" +
//                "                    <p>\n" +
//                "                        Indicate the revenue you want to pay for and present the Invoice Number on the top right corner of this document. Pay the total amount. Get e-payment print out from the bank and present at  Ministry of Commerce and Industry headquarters for your revenue receipt. Contact Information: You can call the following numbers for enquiries: 08069806078 Or Email: info@tax.services.an.gov.ng\n" +
//                "                    </p>\n" +
//                "                </div>\n" +
//                "                <div style=\"font-size: 12px;\">\n" +
//                "                    <p> <span style=\"color:red; font-weight:bold;\">PLEASE NOTE: </span>Tariff plan can be seen displayed in all Ministry of Commerce and Industry offices OR please visit tax.services.an.gov.ng</p>\n" +
//                "                    <p style=\"border: 2px dotted red; padding:20px\">\n" +
//                "                        In line with Anambra State Revenue Administration Law 2010, you are hearby\n" +
//                "                        requested to pay the sum mentioned above. Failure to pay within 7 days from the date of service of this notice shall lead to enforcement, court prosecution and/or other legal action\n" +
//                "                    </p>\n" +
//                "                    <div style=\"text-align:center\">\n" +
//                "                        <p style=\"margin:0; font-size:13px; font-weight:bold;\">RICHARD MADIEBO</p>\n" +
//                "                        <p style=\"margin:0; font-size:13px; font-weight:bold;\">EXECUTIVE CHAIRMAN, IVAS</p>\n" +
//                "                    </div>\n" +
//                "                </div>\n" +
//                "            </div>\n" +
//                "\n" +
//                "        </div>";
//    }

}
