package BackEnd;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class OTP1Service {
    public boolean verifyOTPAndChangePassword(String inputOTP, String realOTP, String username, String newPassword) {
        if (!inputOTP.equals(realOTP)) {
            return false;
        }

        try (Connection conn = ketnoiCSDL.getConnection()) {
            String sql = "UPDATE taikhoan SET Matkhau = ? WHERE TenDangNhap = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newPassword);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }
}
