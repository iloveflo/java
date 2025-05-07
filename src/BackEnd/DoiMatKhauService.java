package BackEnd;

import java.sql.*;
import java.util.Properties;
import jakarta.mail.Message;
import javax.swing.*;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import GUI.OTP1;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class DoiMatKhauService {

    public static boolean doiMatKhau(String username, String oldPassword, String newPassword) {
        JComponent parent= new JComponent(){};
        if (username.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Hãy nhập đầy đủ thông tin", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try (Connection conn = ketnoiCSDL.getConnection()) {
            // 1. Kiểm tra mật khẩu cũ
            String checkQuery = "SELECT MatKhau FROM taikhoan WHERE TenDangNhap = ?";
            try (PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next() || !rs.getString("MatKhau").equals(oldPassword)) {
                    JOptionPane.showMessageDialog(parent, "Sai mật khẩu cũ", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }

            // 2. Lấy email
            String emailQuery = """
                SELECT Email FROM (
                    SELECT Email FROM khachhang WHERE MaTaiKhoan = (SELECT MaTaiKhoan FROM taikhoan WHERE TenDangNhap = ?)
                    UNION
                    SELECT Email FROM nhanvien WHERE MaTaiKhoan = (SELECT MaTaiKhoan FROM taikhoan WHERE TenDangNhap = ?)
                ) AS Emails LIMIT 1;
            """;
            String email = null;
            try (PreparedStatement stmt = conn.prepareStatement(emailQuery)) {
                stmt.setString(1, username);
                stmt.setString(2, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) email = rs.getString("Email");
            }

            if (email == null || email.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Không tìm thấy email của người dùng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // 3. Tạo và gửi mã OTP
            String otp = String.valueOf((int)(Math.random() * 900000 + 100000));
            sendOtpToEmail(email, otp); // bạn cần tạo class SendMailHelper
            JOptionPane.showMessageDialog(parent, "Mã OTP đã được gửi đến: " + email);

            // 4. Mở form OTP
            GUI.OTP1 otpForm = new OTP1(otp, username, newPassword);
            otpForm.setVisible(true);

            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, "Lỗi: " + ex.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    public static void sendOtpToEmail(String toEmail, String otp) {
        final String fromEmail = "binha10k56@gmail.com"; // Email của bạn
        final String password = "";     // Mật khẩu ứng dụng

        // Cấu hình các thuộc tính SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Xác thực tài khoản gửi
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };

        // Tạo session gửi email
        Session session = Session.getInstance(props, auth);

        try {
            // Tạo nội dung email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject("Mã OTP xác nhận");
            message.setText("Mã OTP của bạn là: " + otp);

            // Gửi email
            Transport.send(message);
            System.out.println("OTP đã được gửi đến email: " + toEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}