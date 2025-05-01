import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.*;

public class SendOTP {

    // Hàm tạo mã OTP ngẫu nhiên
    public static String generateOTP(int length) {
        String numbers = "0123456789";
        Random rnd = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < length; i++) {
            otp.append(numbers.charAt(rnd.nextInt(numbers.length())));
        }

        return otp.toString();
    }

    // Hàm gửi email chứa OTP
    public static void sendEmail(String toEmail, String otp) {
        final String fromEmail = "binha10k56@gmail.com"; // Thay bằng email của bạn
        final String password = "eadb mfdp bgdc qtdt";     // Dùng App Password của Gmail

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(toEmail)
            );
            message.setSubject("Your OTP Code");
            message.setText("Your OTP is: " + otp);

            Transport.send(message);
            System.out.println("OTP sent to email successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // Main để chạy
    public static void main(String[] args) {
        String otp = generateOTP(6);
        sendEmail("26a4040713@hvnh.edu.vn", otp); // Thay bằng email người nhận
    }
}
