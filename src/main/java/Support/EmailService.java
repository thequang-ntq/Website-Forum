package Support;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static String SENDER_EMAIL;
    private static String SENDER_PASSWORD;
    
    // Load cấu hình từ file properties
    static {
        Properties config = new Properties();
        InputStream input = null;
        
        try {
            // Thử load từ classpath trước (file trong src/)
            input = EmailService.class.getClassLoader()
                .getResourceAsStream("email.properties");
            
            if (input == null) {
                // Nếu không tìm thấy, thử đường dẫn tương đối từ project root
                String projectPath = System.getProperty("user.dir");
                String filePath = projectPath + "/src/main/java/email.properties";
                input = new FileInputStream(filePath);
            }
            
            if (input != null) {
                config.load(input);
                SENDER_EMAIL = config.getProperty("email.sender");
                SENDER_PASSWORD = config.getProperty("email.password");
                System.out.println("Email configuration loaded successfully!");
            } 
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file email.properties: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static String generatePIN() {
        Random random = new Random();
        int pin = 100000 + random.nextInt(900000);
        return String.valueOf(pin);
    }
    
    public static boolean sendPIN(String recipientEmail, String pin) {
        if (SENDER_EMAIL == null || SENDER_PASSWORD == null) {
            System.err.println("Email configuration not loaded!");
            return false;
        }
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            // Cấu hình để dùng Email mình gửi đến mail họ
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            // Tiêu đề Email
            message.setSubject("Mã PIN xác thực - Website Forum");
            // Nội dung Email
            String htmlContent = 
                "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "</head>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>" +
                "<h2 style='color: #667eea; text-align: center; margin-bottom: 20px;'>Mã PIN Xác Thực</h2>" +
                "<p style='color: #333; font-size: 16px;'>Xin chào,</p>" +
                "<p style='color: #333; font-size: 16px;'>Bạn đã yêu cầu đặt lại mật khẩu. Đây là mã PIN xác thực của bạn:</p>" +
                "<div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 20px; border-radius: 8px; text-align: center; margin: 30px 0;'>" +
                "<h1 style='color: white; letter-spacing: 10px; margin: 0; font-size: 36px;'>" + pin + "</h1>" +
                "</div>" +
                "<p style='color: #666; font-size: 14px;'><strong>Lưu ý:</strong> Mã PIN có hiệu lực trong <strong>5 phút</strong>.</p>" +
                "<p style='color: #999; font-size: 12px; margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee;'>" +
                "Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này.<br>" +
                "Email này được gửi tự động, vui lòng không trả lời." +
                "</p>" +
                "</div>" +
                "</body>" +
                "</html>";
            
            message.setContent(htmlContent, "text/html; charset=UTF-8");
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        //Phần trước @ (username) cho phép chữ, số, ký tự đặc biệt + _ . - và ít nhất 1 ký tự. VD: levana
        //Phần ký tự @ bắt buộc có
        //Phần ngay sau @ là tên miền cho phép chữ, số, dấu . và dấu - . VD: gmail
        //Phần tiếp là dấu chấm trước phần mở rộng.
        //Phần mở rộng tên miền sau dấu chấm, chỉ chữ, ít nhất 2 ký tự. VD: com, edu, vn
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }
}