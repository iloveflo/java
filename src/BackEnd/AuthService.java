package BackEnd;

import javax.swing.*;

import GUI.Quenmatkhau;

import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class AuthService {
    public static void handleLogin(String username, String password, JFrame parentFrame) {
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

                    SessionManager.setSession(maTK, loaiTK);
                    JOptionPane.showMessageDialog(parentFrame, "Đăng nhập thành công\nMã: " + maTK + "\nLoại: " + loaiTK);

                    parentFrame.dispose();
                    /*switch (loaiTK) {
                        case "Admin":
                            rs.close(); // Đóng ResultSet
                            String updateQuery = "UPDATE TaiKhoan SET DangNhap = 1 WHERE MaTaiKhoan = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                                updateStmt.setString(1, SessionManager.getMaTaiKhoan());
                                updateStmt.executeUpdate();
                            }
                            new ui.AdminForm().setVisible(true);
                            break;
                        case "KhachHang":
                            rs.close(); // Đóng ResultSet
                            String updateQuery1 = "UPDATE TaiKhoan SET DangNhap = 1 WHERE MaTaiKhoan = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery1)) {
                                updateStmt.setString(1, SessionManager.getMaTaiKhoan());
                                updateStmt.executeUpdate();
                            }
                            new ui.KhachHangForm().setVisible(true);
                            break;
                        case "NhanVien":
                            rs.close(); // Đóng ResultSet
                            String updateQuery2 = "UPDATE TaiKhoan SET DangNhap = 1 WHERE MaTaiKhoan = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery2)) {
                                updateStmt.setString(1, SessionManager.getMaTaiKhoan());
                                updateStmt.executeUpdate();
                            }
                            new ui.NhanVienForm().setVisible(true);
                            break;
                        default:
                            JOptionPane.showMessageDialog(parentFrame, "Loại tài khoản không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }*/
                } else {
                    JOptionPane.showMessageDialog(parentFrame, "Sai tài khoản hoặc mật khẩu", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentFrame, "Lỗi kết nối: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void addLoginEvents(JFrame parentFrame, JButton btnLogin, JCheckBox cboxRemember, JPasswordField txtPass, JTextField txtUser, JButton btnRegister, JButton btnQuenmatkhau) {
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                handleLogin(txtUser.getText(), String.valueOf(txtPass.getPassword()), parentFrame);
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

