package BackEnd;

import javax.swing.*;
import java.awt.*;
import GUI.Quenmatkhau;
import java.sql.*;
import java.util.Random;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class AuthService {
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

    public static boolean checkcapcha(JTextField txtCaptcha){
        String captchaInput = txtCaptcha.getText();
        return captchaInput.equals(currentCaptchaCode);
    }

    public static void handleLogin(String username, String password, JFrame parentFrame,JTextField capcha) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "Hãy nhập đầy đủ thông tin", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = ketnoiCSDL.getConnection()) {
            String sql = "SELECT MaTaiKhoan, LoaiTaiKhoan, DangNhap FROM TaiKhoan WHERE TenDangNhap = ? AND MatKhau = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String maTK = rs.getString("MaTaiKhoan");
                    String loaiTK = rs.getString("LoaiTaiKhoan");
                    boolean dangNhap = rs.getBoolean("DangNhap");

                    if (dangNhap) {
                        JOptionPane.showMessageDialog(null, "Tài khoản đang đăng nhập ở thiết bị khác!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if(!checkcapcha(capcha)){
                        JOptionPane.showMessageDialog(parentFrame, "Sai mã bảo vệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    SessionManager.setSession(maTK, loaiTK);
                    JOptionPane.showMessageDialog(parentFrame, "Đăng nhập thành công\nMã: " + maTK + "\nLoại: " + loaiTK);

                    parentFrame.dispose();
                    switch (loaiTK) {
                        case "Admin":
                            rs.close(); // Đóng ResultSet
                            String updateQuery = "UPDATE TaiKhoan SET DangNhap = 1 WHERE MaTaiKhoan = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                                updateStmt.setString(1, SessionManager.getMaTaiKhoan());
                                updateStmt.executeUpdate();
                            }
                            new GUI.Menu().setVisible(true);
                            break;
                        case "KhachHang":
                            rs.close(); // Đóng ResultSet
                            String updateQuery1 = "UPDATE TaiKhoan SET DangNhap = 1 WHERE MaTaiKhoan = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery1)) {
                                updateStmt.setString(1, SessionManager.getMaTaiKhoan());
                                updateStmt.executeUpdate();
                            }
                            new GUI.KhachhangForm().setVisible(true);
                            break;
                        case "NhanVien":
                            rs.close(); // Đóng ResultSet
                            String updateQuery2 = "UPDATE TaiKhoan SET DangNhap = 1 WHERE MaTaiKhoan = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery2)) {
                                updateStmt.setString(1, SessionManager.getMaTaiKhoan());
                                updateStmt.executeUpdate();
                            }
                            new GUI.NhanvienForm().setVisible(true);
                            break;
                        default:
                            JOptionPane.showMessageDialog(parentFrame, "Loại tài khoản không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(parentFrame, "Sai tài khoản hoặc mật khẩu", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentFrame, "Lỗi kết nối: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void addLoginEvents(JFrame parentFrame, JButton btnLogin, JCheckBox cboxRemember, JPasswordField txtPass, JTextField txtUser, JButton btnRegister, JButton btnQuenmatkhau,JTextField txtCapcha) {
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                handleLogin(txtUser.getText(), String.valueOf(txtPass.getPassword()), parentFrame,txtCapcha);
            }
        });
        parentFrame.getRootPane().setDefaultButton(btnLogin); // ấn enter để đăng nhập
    
        cboxRemember.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                if (cboxRemember.isSelected()) {
                    txtPass.setEchoChar((char) 0); // Hiện mật khẩu
                } else {
                    txtPass.setEchoChar('\u2022'); // Ẩn mật khẩu
                }
            }
        });
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                parentFrame.dispose();
                new GUI.register().setVisible(true);
            }
        });
        btnQuenmatkhau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parentFrame.dispose();
                new Quenmatkhau().setVisible(true);
            }
        });
    }
}

