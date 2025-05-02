package BackEnd;

import javax.swing.*;
import java.sql.*;
import GUI.*;

public class OTPService {

    private static String maOTP;
    private static String tenDangNhap;

    // Lưu thông tin từ form trước
    public static void setData(String username, String otp) {
        tenDangNhap = username;
        maOTP = otp;
    }

    // Xác thực OTP và cập nhật mật khẩu
    public static void xuLyXacThuc(String nhapOTP, JFrame currentFrame) {
        if (nhapOTP == null || nhapOTP.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập mã OTP.");
            return;
        }

        if (!nhapOTP.equals(maOTP)) {
            JOptionPane.showMessageDialog(null, "Mã OTP không đúng.");
            return;
        }

        try (Connection conn = ketnoiCSDL.getConnection()) {
            String newPass = "admin123"; // Nếu bạn dùng mã hóa mật khẩu thì mã hóa tại đây

            String sql = "UPDATE taikhoan SET MatKhau = ? WHERE TenDangNhap = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newPass);
            stmt.setString(2, tenDangNhap);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Đặt lại mật khẩu thành công!\nMật khẩu mới là: admin123");
                new Start().setVisible(true);
                currentFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Không tìm thấy tài khoản.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi: " + e.getMessage());
        }
    }

    // Quay lại Start.java
    public static void xuLyQuayLai(JFrame currentFrame) {
        new Start().setVisible(true);
        currentFrame.dispose();
    }
}
