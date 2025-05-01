package BackEnd;

import java.sql.*;
import java.util.Random;
import javax.swing.*;
import java.awt.*;

public class RegisterService {

    private static int currentCaptchaId = -1;
    private static String currentCaptchaCode = "";
    private static final Random random = new Random();

    public static void loadRandomCaptcha(JLabel lblCaptchaImage) {
        try (Connection conn = ketnoiCSDL.getConnection()) {
            String countQuery = "SELECT COUNT(*) FROM CapCha";
            try (PreparedStatement countStmt = conn.prepareStatement(countQuery);
                 ResultSet countRs = countStmt.executeQuery()) {

                if (countRs.next()) {
                    int count = countRs.getInt(1);
                    if (count == 0) {
                        JOptionPane.showMessageDialog(null, "Không có CAPTCHA nào trong cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    int randomIndex = random.nextInt(count);

                    String query = "SELECT MaAnh, LinkAnh, KetQua FROM CapCha LIMIT ?, 1";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setInt(1, randomIndex);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                currentCaptchaId = rs.getInt("MaAnh");
                                currentCaptchaCode = rs.getString("KetQua");
                                String linkAnh = rs.getString("LinkAnh");
                                lblCaptchaImage.setIcon(new ImageIcon(new ImageIcon(linkAnh).getImage().getScaledInstance(150, 50, Image.SCALE_SMOOTH)));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi load CAPTCHA: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void changeCaptcha(JLabel lblCaptchaImage) {
        int previousId = currentCaptchaId;
        do {
            loadRandomCaptcha(lblCaptchaImage);
        } while (currentCaptchaId == previousId);
    }

    public static void goBack(JFrame currentFrame) {
        GUI.Start mainForm = new GUI.Start();
        mainForm.setVisible(true);
        currentFrame.dispose();
    }
    public static void togglePasswordVisibility(JPasswordField passwordField, JCheckBox checkBox) {
        if (checkBox.isSelected()) {
            passwordField.setEchoChar((char) 0); // Hiện mật khẩu
        } else {
            passwordField.setEchoChar('\u2022'); // Dấu chấm đen
        }
    }
    

    public static void registerUser(JTextField txtFullName, JTextField txtUsername, JPasswordField txtPassword,
                                    JPasswordField txtConfirmPassword, JTextField txtEmail, JTextField txtPhone,
                                    JTextField txtAddress, JTextField txtAccountCode, JTextField txtCaptcha, JFrame currentFrame) {

        String hoTen = txtFullName.getText().trim();
        String userName = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String rePassword = new String(txtConfirmPassword.getPassword()).trim();
        String email = txtEmail.getText().trim();
        String soDienThoai = txtPhone.getText().trim();
        String diaChi = txtAddress.getText().trim();
        String maTaiKhoan = txtAccountCode.getText().trim();
        String captchaInput = txtCaptcha.getText().trim();

        if (hoTen.isEmpty() || userName.isEmpty() || password.isEmpty() || rePassword.isEmpty()
            || email.isEmpty() || soDienThoai.isEmpty() || diaChi.isEmpty() || maTaiKhoan.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Hãy nhập đầy đủ thông tin!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!soDienThoai.matches("^\\d{10}$")) {
            JOptionPane.showMessageDialog(null, "Số điện thoại phải là 10 chữ số!");
            return;
        }        

        if (!password.equals(rePassword)) {
            JOptionPane.showMessageDialog(null, "Password và Re-enter Password chưa giống nhau!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 8 || password.length() > 16) {
            JOptionPane.showMessageDialog(null, "Mật khẩu không được ít hơn 8 hoặc vượt quá 16 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!captchaInput.equals(currentCaptchaCode)) {
            JOptionPane.showMessageDialog(null, "CAPTCHA không đúng! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = ketnoiCSDL.getConnection()) {
            conn.setAutoCommit(false);

            String checkQuery = "SELECT " +
        "(SELECT COUNT(*) FROM TaiKhoan WHERE TenDangNhap = ? OR MaTaiKhoan = ?) AS CountTaiKhoan, " +
        "(SELECT COUNT(*) FROM (SELECT SoDienThoai FROM KhachHang WHERE SoDienThoai = ? UNION ALL SELECT SoDienThoai FROM NhanVien WHERE SoDienThoai = ?) AS TempSDT) AS CountSDT, " +
        "(SELECT COUNT(*) FROM (SELECT Email FROM KhachHang WHERE Email = ? UNION ALL SELECT Email FROM NhanVien WHERE Email = ?) AS TempEmail) AS CountEmail";

            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, userName);
                checkStmt.setString(2, maTaiKhoan);
                checkStmt.setString(3, soDienThoai);
                checkStmt.setString(4, soDienThoai);
                checkStmt.setString(5, email);
                checkStmt.setString(6, email);

                boolean hasResult = checkStmt.execute();
                int countTaiKhoan = 0, countSDT = 0, countEmail = 0;

                if (hasResult) {
                    try (ResultSet rs = checkStmt.getResultSet()) {
                        if (rs.next()) countTaiKhoan = rs.getInt(1);
                    }
                    if (checkStmt.getMoreResults()) {
                        try (ResultSet rs = checkStmt.getResultSet()) {
                            if (rs.next()) countSDT = rs.getInt(1);
                        }
                    }
                    if (checkStmt.getMoreResults()) {
                        try (ResultSet rs = checkStmt.getResultSet()) {
                            if (rs.next()) countEmail = rs.getInt(1);
                        }
                    }
                }

                if (countTaiKhoan > 0) {
                    JOptionPane.showMessageDialog(null, "Tên tài khoản hoặc mã tài khoản đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    conn.rollback();
                    return;
                }
                if (countSDT > 0) {
                    JOptionPane.showMessageDialog(null, "Số điện thoại đã tồn tại trong hệ thống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    conn.rollback();
                    return;
                }
                if (countEmail > 0) {
                    JOptionPane.showMessageDialog(null, "Email đã tồn tại trong hệ thống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    conn.rollback();
                    return;
                }
            }

            String insertTaiKhoan = "INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, LoaiTaiKhoan) VALUES (?, ?, ?, 'KhachHang')";
            try (PreparedStatement insertTaiKhoanStmt = conn.prepareStatement(insertTaiKhoan)) {
                insertTaiKhoanStmt.setString(1, maTaiKhoan);
                insertTaiKhoanStmt.setString(2, userName);
                insertTaiKhoanStmt.setString(3, password);
                insertTaiKhoanStmt.executeUpdate();
            }

            String insertKhachHang = "INSERT INTO KhachHang (MaKhachHang, TenKhach, SoDienThoai, DiaChi, MaTaiKhoan, Email) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertKhachHangStmt = conn.prepareStatement(insertKhachHang)) {
                insertKhachHangStmt.setString(1, maTaiKhoan);
                insertKhachHangStmt.setString(2, hoTen);
                insertKhachHangStmt.setString(3, soDienThoai);
                insertKhachHangStmt.setString(4, diaChi);
                insertKhachHangStmt.setString(5, maTaiKhoan);
                insertKhachHangStmt.setString(6, email);
                insertKhachHangStmt.executeUpdate();
            }

            conn.commit();

            JOptionPane.showMessageDialog(null, "Đăng ký thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            GUI.Start mainForm = new GUI.Start();
            mainForm.setVisible(true);
            currentFrame.dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi đăng ký: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
