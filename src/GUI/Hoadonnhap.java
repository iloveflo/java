/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GUI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import BackEnd.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author Neo 16
 */
public class Hoadonnhap extends javax.swing.JFrame {

    /**
     * Creates new form Khachhang
     */
    public Hoadonnhap() {
        initComponents();
        loadNhaCungCap();
        loadHoaDonNhapData();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Ngăn đóng mặc định
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                capNhatDangNhapVaThoat();
            }
        });
        btnThoat.addActionListener(e -> {
            new Menu().setVisible(true);
            dispose();
        });
        tblHoadonnhap.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = tblHoadonnhap.getSelectedRow();
            if (!e.getValueIsAdjusting() && selectedRow >= 0) {
                Object soHD = tblHoadonnhap.getValueAt(selectedRow, 0);
                txtSohoadonnhap.setText(soHD != null ? soHD.toString() : "");
        
                Object maNV = tblHoadonnhap.getValueAt(selectedRow, 1);
                txtManhanvien.setText(maNV != null ? maNV.toString() : "");
        
                Object tenNCC = tblHoadonnhap.getValueAt(selectedRow, 2);
                if (tenNCC != null && boxNhacungcap.getItemCount() > 0) {
                    boxNhacungcap.setSelectedItem(tenNCC.toString());
                } else {
                    boxNhacungcap.setSelectedIndex(-1);
                }
        
                Object maQA = tblHoadonnhap.getValueAt(selectedRow, 3);
                txtMaquanao.setText(maQA != null ? maQA.toString() : "");
        
                Object sl = tblHoadonnhap.getValueAt(selectedRow, 4);
                txtSoluongnhap.setText(sl != null ? sl.toString() : "");
        
                Object dongia = tblHoadonnhap.getValueAt(selectedRow, 5);
                txtDongia.setText(dongia != null ? dongia.toString() : "");
        
                Object giamgia = tblHoadonnhap.getValueAt(selectedRow, 6);
                txtGiamgia.setText(giamgia != null ? giamgia.toString() : "");
            }
        });
        btnLaphoadon.addActionListener(e -> {
            int maQuanAo = Integer.parseInt(txtMaquanao.getText());
            int maNhanVien = Integer.parseInt(txtManhanvien.getText());
            int soLuong = Integer.parseInt(txtSoluongnhap.getText());
            double donGia = Double.parseDouble(txtDongia.getText());
            double giamGia = Double.parseDouble(txtGiamgia.getText());
            String tenNCC = boxNhacungcap.getSelectedItem().toString();
        
            boolean result = HoaDonNhapService.lapHoaDonNhap(maQuanAo, maNhanVien, soLuong, donGia, giamGia, tenNCC);
            if (result) {
                JOptionPane.showMessageDialog(null, "Lập hóa đơn thành công!");
                loadHoaDonNhapData();
            }
        });
        btnTimkiem.addActionListener(e -> {
            String soHoaDon = txtSohoadonnhap.getText();
            String tenNCC = boxNhacungcap.getSelectedItem().toString();
            
            // Gọi hàm tìm kiếm từ HoaDonNhapDAO và cập nhật dữ liệu lên bảng
            DefaultTableModel model = HoaDonNhapService.timKiemHoaDonNhap(soHoaDon, tenNCC);
            tblHoadonnhap.setModel(model);
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
    private void loadNhaCungCap() {
        try (Connection conn = ketnoiCSDL.getConnection()) {
            String query = "SELECT MaNCC, TenNCC FROM NhaCungCap";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            boxNhacungcap.removeAllItems(); // Xóa các mục cũ nếu có

            while (rs.next()) {
                String tenNCC = rs.getString("TenNCC");
                boxNhacungcap.addItem(tenNCC);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi tải nhà cung cấp: " + e.getMessage());
        }
    }
    private void loadHoaDonNhapData() {
        DefaultTableModel model = (DefaultTableModel) tblHoadonnhap.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        String query = """
            SELECT hd.SoHoaDonNhap, hd.MaNhanVien, ncc.TenNCC, ct.MaQuanAo, 
                ct.SoLuong, ct.DonGia, ct.GiamGia, 
                ct.SoLuong * ct.DonGia * (1 - ct.GiamGia / 100) AS ThanhTien
            FROM HoaDonNhap hd
            JOIN NhaCungCap ncc ON hd.MaNCC = ncc.MaNCC
            JOIN ChiTietHoaDonNhap ct ON hd.SoHoaDonNhap = ct.SoHoaDonNhap
        """;

        try (Connection conn = ketnoiCSDL.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                    rs.getString("SoHoaDonNhap"),
                    rs.getString("MaNhanVien"),
                    rs.getString("TenNCC"),
                    rs.getString("MaQuanAo"),
                    rs.getInt("SoLuong"),
                    rs.getDouble("DonGia"),
                    rs.getFloat("GiamGia"),
                    rs.getDouble("ThanhTien")
                };
                model.addRow(row);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi tải dữ liệu hóa đơn nhập: " + e.getMessage());
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
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblHoadonnhap = new javax.swing.JTable();
        lblNhacungcap = new javax.swing.JLabel();
        lblMaquanao = new javax.swing.JLabel();
        lblDongia = new javax.swing.JLabel();
        lblGiamgia = new javax.swing.JLabel();
        lblSoluongnhap = new javax.swing.JLabel();
        txtMaquanao = new javax.swing.JTextField();
        txtDongia = new javax.swing.JTextField();
        txtGiamgia = new javax.swing.JTextField();
        txtSoluongnhap = new javax.swing.JTextField();
        txtSohoadonnhap = new javax.swing.JTextField();
        btnLaphoadon = new javax.swing.JButton();
        btnTimkiem = new javax.swing.JButton();
        btnThoat = new javax.swing.JButton();
        txtManhanvien = new javax.swing.JTextField();
        lblManhanvien = new javax.swing.JLabel();
        lblSohoadonnhap1 = new javax.swing.JLabel();
        boxNhacungcap = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(200, 173, 127));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("DANH SÁCH HÓA ĐƠN NHẬP");

        tblHoadonnhap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                // Có thể để trống hoặc thêm dòng mẫu nếu cần
            },
            new String [] {
                "Số hóa đơn nhập", "Mã nhân viên", "Tên nhà cung cấp", "Mã quần áo",
                "Số lượng", "Đơn giá", "Giảm giá", "Thành tiền"
            }
        ));

        jScrollPane1.setViewportView(tblHoadonnhap);

        lblNhacungcap.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblNhacungcap.setText("Nhà cung cấp:");

        lblMaquanao.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMaquanao.setText("Mã quần áo:");

        lblDongia.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblDongia.setText("Đơn giá:");

        lblGiamgia.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblGiamgia.setText("Giảm giá(%):");

        lblSoluongnhap.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblSoluongnhap.setText("Số lượng nhập:");

        btnLaphoadon.setText("Lập hóa đơn");
        btnLaphoadon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXuathoadonActionPerformed(evt);
            }
        });

        btnTimkiem.setText("Tìm kiếm");

        btnThoat.setText("Thoát");

        lblManhanvien.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblManhanvien.setText("Mã nhân viên:");

        lblSohoadonnhap1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblSohoadonnhap1.setText("Số hóa đơn nhập:");

        boxNhacungcap.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(407, 407, 407)
                .addComponent(btnLaphoadon, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(126, 126, 126)
                .addComponent(btnThoat, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(394, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 989, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblMaquanao)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMaquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblManhanvien)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtManhanvien, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblNhacungcap)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(boxNhacungcap, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblSohoadonnhap1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSohoadonnhap, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnTimkiem)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblDongia))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblGiamgia)
                                    .addComponent(lblSoluongnhap))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDongia, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtGiamgia, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSoluongnhap, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(114, 114, 114))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel1)
                        .addGap(29, 29, 29)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblDongia)
                            .addComponent(txtDongia, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSohoadonnhap, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnTimkiem, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblSohoadonnhap1)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblGiamgia)
                            .addComponent(txtGiamgia, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblSoluongnhap)
                                    .addComponent(txtSoluongnhap, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(68, 68, 68))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtMaquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblMaquanao))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblManhanvien)
                                    .addComponent(txtManhanvien, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30)))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnLaphoadon, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnThoat, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(51, 51, 51))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNhacungcap)
                            .addComponent(boxNhacungcap, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnXuathoadonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXuathoadonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnXuathoadonActionPerformed

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
            java.util.logging.Logger.getLogger(Hoadonnhap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Hoadonnhap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Hoadonnhap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Hoadonnhap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Hoadonnhap().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> boxNhacungcap;
    private javax.swing.JButton btnThoat;
    private javax.swing.JButton btnTimkiem;
    private javax.swing.JButton btnLaphoadon;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDongia;
    private javax.swing.JLabel lblGiamgia;
    private javax.swing.JLabel lblManhanvien;
    private javax.swing.JLabel lblMaquanao;
    private javax.swing.JLabel lblNhacungcap;
    private javax.swing.JLabel lblSohoadonnhap1;
    private javax.swing.JLabel lblSoluongnhap;
    private javax.swing.JTable tblHoadonnhap;
    private javax.swing.JTextField txtDongia;
    private javax.swing.JTextField txtGiamgia;
    private javax.swing.JTextField txtManhanvien;
    private javax.swing.JTextField txtMaquanao;
    private javax.swing.JTextField txtSohoadonnhap;
    private javax.swing.JTextField txtSoluongnhap;
    // End of variables declaration//GEN-END:variables
}
