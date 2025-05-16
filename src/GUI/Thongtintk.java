/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GUI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import BackEnd.*;

/**
 *
 * @author Neo 16
 */
public class Thongtintk extends javax.swing.JFrame {

    /**
     * Creates new form Thongtintk
     */
    public Thongtintk() {
        initComponents();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Ngăn đóng mặc định
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                capNhatDangNhapVaThoat();
            }
        });
        loadThongTinTaiKhoan();
        btnThoat.addActionListener(e -> {
            String loaiTaiKhoan = SessionManager.getLoaiTaiKhoan();
            if ("KhachHang".equals(loaiTaiKhoan)) {
                new KhachhangForm().setVisible(true);
                dispose(); // ẩn hoặc đóng frame hiện tại
            } else if ("NhanVien".equals(loaiTaiKhoan)) {
                new NhanvienForm().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Loại tài khoản không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnSua.addActionListener(e -> {
            txtHovaten.setEnabled(true);
            txtSDT.setEnabled(true);
            txtEmail.setEnabled(true);
            txtDiachi.setEnabled(true);

            btnLuu.setEnabled(true);
            btnHuy.setEnabled(true);
            btnSua.setEnabled(false);
        });

        btnHuy.addActionListener(e -> {
            loadThongTinTaiKhoan();

            // Vô hiệu hóa các ô nhập
            txtHovaten.setEnabled(false);
            txtSDT.setEnabled(false);
            txtEmail.setEnabled(false);
            txtDiachi.setEnabled(false);

            btnLuu.setEnabled(false);
            btnHuy.setEnabled(false);
            btnSua.setEnabled(true);
        });

        btnLuu.addActionListener(e -> {
            String maTK = txtMatk.getText().trim();
            String email = txtEmail.getText().trim();
            String sdt = txtSDT.getText().trim();
            String hoten = txtHovaten.getText().trim();
            String diachi = txtDiachi.getText().trim();

            try (Connection conn = ketnoiCSDL.getConnection()) {
                // Kiểm tra trùng email hoặc SDT (trừ chính mình)
                String queryCheck = """
                    SELECT COUNT(*) FROM (
                        SELECT Email FROM nhanvien WHERE Email = ? AND MaTaiKhoan != ?
                        UNION ALL
                        SELECT Email FROM khachhang WHERE Email = ? AND MaTaiKhoan != ?
                        UNION ALL
                        SELECT SoDienThoai FROM nhanvien WHERE SoDienThoai = ? AND MaTaiKhoan != ?
                        UNION ALL
                        SELECT SoDienThoai FROM khachhang WHERE SoDienThoai = ? AND MaTaiKhoan != ?
                    ) AS Duplicates
                """;
                try (PreparedStatement psCheck = conn.prepareStatement(queryCheck)) {
                    psCheck.setString(1, email);
                    psCheck.setString(2, maTK);
                    psCheck.setString(3, email);
                    psCheck.setString(4, maTK);
                    psCheck.setString(5, sdt);
                    psCheck.setString(6, maTK);
                    psCheck.setString(7, sdt);
                    psCheck.setString(8, maTK);

                    ResultSet rs = psCheck.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(null, "Email hoặc số điện thoại đã tồn tại.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                // Lấy loại tài khoản
                String loai = "";
                try (PreparedStatement psLoai = conn.prepareStatement("SELECT LoaiTaiKhoan FROM taikhoan WHERE MaTaiKhoan = ?")) {
                    psLoai.setString(1, maTK);
                    ResultSet rsLoai = psLoai.executeQuery();
                    if (rsLoai.next()) {
                        loai = rsLoai.getString("LoaiTaiKhoan");
                    }
                }

                // Cập nhật dữ liệu
                String queryUpdate = "";
                if ("NhanVien".equals(loai)) {
                    queryUpdate = "UPDATE nhanvien SET TenNhanVien = ?, SoDienThoai = ?, Email = ?, DiaChi = ? WHERE MaTaiKhoan = ?";
                } else if ("KhachHang".equals(loai)) {
                    queryUpdate = "UPDATE khachhang SET TenKhach = ?, SoDienThoai = ?, Email = ?, DiaChi = ? WHERE MaTaiKhoan = ?";
                }

                if (!queryUpdate.isEmpty()) {
                    try (PreparedStatement psUpdate = conn.prepareStatement(queryUpdate)) {
                        psUpdate.setString(1, hoten);
                        psUpdate.setString(2, sdt);
                        psUpdate.setString(3, email);
                        psUpdate.setString(4, diachi);
                        psUpdate.setString(5, maTK);

                        int result = psUpdate.executeUpdate();
                        if (result > 0) {
                            JOptionPane.showMessageDialog(null, "Cập nhật thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                            btnHuy.doClick(); // Gọi lại sự kiện Hủy để reload lại
                        } else {
                            JOptionPane.showMessageDialog(null, "Không có dữ liệu nào được cập nhật.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật thông tin: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void capNhatDangNhap() {
        try (Connection conn = ketnoiCSDL.getConnection()) {
            String sql = "UPDATE taikhoan SET DangNhap = 0 WHERE MaTaiKhoan = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, SessionManager.getMaTaiKhoan());
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void capNhatDangNhapVaThoat() {
        capNhatDangNhap();
        SessionManager.clearSession();
        System.exit(0);
    }

    private void loadThongTinTaiKhoan() {
        String maTaiKhoan = SessionManager.getMaTaiKhoan();
        if (maTaiKhoan == null || maTaiKhoan.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy mã tài khoản đăng nhập.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = ketnoiCSDL.getConnection()) {
            String tenDangNhap = "";
            String loaiTaiKhoan = "";

            // Lấy thông tin đăng nhập
            String queryTaiKhoan = "SELECT TenDangNhap, LoaiTaiKhoan FROM taikhoan WHERE MaTaiKhoan = ?";
            try (PreparedStatement stmt = conn.prepareStatement(queryTaiKhoan)) {
                stmt.setString(1, maTaiKhoan);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    tenDangNhap = rs.getString("TenDangNhap");
                    loaiTaiKhoan = rs.getString("LoaiTaiKhoan");
                } else {
                    JOptionPane.showMessageDialog(null, "Tài khoản không tồn tại trong hệ thống!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // Truy vấn thông tin người dùng theo loại tài khoản
            String queryThongTin;
            if (loaiTaiKhoan.equals("NhanVien")) {
                queryThongTin = "SELECT TenNhanVien AS Ten, SoDienThoai, Email, DiaChi, MaTaiKhoan FROM nhanvien WHERE MaTaiKhoan = ? LIMIT 1";
            } else if (loaiTaiKhoan.equals("KhachHang")) {
                queryThongTin = "SELECT TenKhach AS Ten, SoDienThoai, Email, DiaChi, MaTaiKhoan FROM khachhang WHERE MaTaiKhoan = ? LIMIT 1";
            } else {
                JOptionPane.showMessageDialog(null, "Loại tài khoản không hợp lệ hoặc chưa hỗ trợ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(queryThongTin)) {
                stmt.setString(1, maTaiKhoan);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    txtHovaten.setText(rs.getString("Ten"));
                    txtSDT.setText(rs.getString("SoDienThoai"));
                    txtEmail.setText(rs.getString("Email"));
                    txtDiachi.setText(rs.getString("DiaChi"));
                    txtTentk.setText(tenDangNhap);
                    txtMatk.setText(rs.getString("MaTaiKhoan"));

                    // Disable fields sau khi load
                    txtHovaten.setEnabled(false);
                    txtSDT.setEnabled(false);
                    txtEmail.setEnabled(false);
                    txtDiachi.setEnabled(false);
                    txtTentk.setEnabled(false);
                    txtMatk.setEnabled(false);
                    btnLuu.setEnabled(false);
                    btnHuy.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(null, "Không tìm thấy thông tin người dùng.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi truy vấn dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        Anh = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtHovaten = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtSDT = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtDiachi = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtTentk = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtMatk = new javax.swing.JTextField();
        btnSua = new javax.swing.JButton();
        btnLuu = new javax.swing.JButton();
        btnHuy = new javax.swing.JButton();
        btnThoat = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(200, 173, 127));

        javax.swing.GroupLayout AnhLayout = new javax.swing.GroupLayout(Anh);
        Anh.setLayout(AnhLayout);
        AnhLayout.setHorizontalGroup(
            AnhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 117, Short.MAX_VALUE)
        );
        AnhLayout.setVerticalGroup(
            AnhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 132, Short.MAX_VALUE)
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("TÀI KHOẢN CỦA BẠN");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Họ và tên:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("SDT: ");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setText("Email: ");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setText("Địa chỉ:");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel6.setText("Tên tài khoản:");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel7.setText("Mã tài khoản:");

        btnSua.setText("Sửa");

        btnLuu.setText("Lưu");

        btnHuy.setText("Hủy");

        btnThoat.setText("Thoát");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtMatk, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnLuu, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnHuy, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 6, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtDiachi, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(Anh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel1))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtHovaten, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtTentk, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addComponent(btnThoat, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jLabel1))
                    .addComponent(Anh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtHovaten, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtDiachi, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtTentk, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtMatk, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLuu, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHuy, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnThoat, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Thongtintk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Thongtintk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Thongtintk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Thongtintk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Thongtintk().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Anh;
    private javax.swing.JButton btnHuy;
    private javax.swing.JButton btnLuu;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThoat;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txtDiachi;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtHovaten;
    private javax.swing.JTextField txtMatk;
    private javax.swing.JTextField txtSDT;
    private javax.swing.JTextField txtTentk;
    // End of variables declaration//GEN-END:variables
}
