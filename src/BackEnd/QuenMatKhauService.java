package BackEnd;

import javax.swing.*;
import java.sql.*;
import java.util.Properties;
import java.util.Random;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import GUI.*;
public class QuenMatKhauService {

    // Gửi mã OTP và chuyển sang form OTP.java
    public static void xuLyGuiMaOTP(String tenDangNhap, JFrame currentFrame) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập tên đăng nhập.");
            return;
        }

        try (Connection conn = ketnoiCSDL.getConnection()) {
            String sql = "SELECT tk.LoaiTaiKhoan, kh.Email AS EmailKH, nv.Email AS EmailNV "
                       + "FROM taikhoan tk "
                       + "LEFT JOIN khachhang kh ON tk.MaTaiKhoan = kh.MaTaiKhoan "
                       + "LEFT JOIN nhanvien nv ON tk.MaTaiKhoan = nv.MaTaiKhoan "
                       + "WHERE tk.TenDangNhap = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, tenDangNhap);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String loai = rs.getString("LoaiTaiKhoan");
                String email = loai.equals("KhachHang") ? rs.getString("EmailKH") : rs.getString("EmailNV");

                if (email == null || email.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Không tìm thấy email cho tài khoản này.");
                    return;
                }

                String otp = generateOTP(6);
                boolean sent = sendEmail(email, otp);

                if (sent) {
                    JOptionPane.showMessageDialog(null, "Mã OTP đã được gửi tới email :"+ email);
                    new OTP(tenDangNhap, otp).setVisible(true);
                    currentFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Gửi email thất bại. Kiểm tra lại kết nối.");
                }

            } else {
                JOptionPane.showMessageDialog(null, "Tên đăng nhập không tồn tại.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi: " + ex.getMessage());
        }
    }

    // Quay lại form Start.java
    public static void xuLyQuayLai(JFrame currentFrame) {
        new Start().setVisible(true);
        currentFrame.dispose();
    }

    // Tạo OTP
    private static String generateOTP(int length) {
        String digits = "0123456789";
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(digits.charAt(random.nextInt(digits.length())));
        }
        return otp.toString();
    }

    // Gửi email
    private static boolean sendEmail(String toEmail, String otp) {
        final String fromEmail = "binha10k56@gmail.com";
        final String password = "";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromEmail));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject("Mã OTP");
            msg.setText("Mã OTP của bạn là: " + otp);
            Transport.send(msg);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
}
