/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import BackEnd.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Neo 16
 */
public class Hoadonban extends javax.swing.JPanel {

    /**
     * Creates new form Hoadonban1
     */
    public Hoadonban() {
        initComponents();
        loadHoaDonBanToTable();
        btnXuathoadon.addActionListener(e -> {
            String soHoaDon = txtSohoadonban.getText().trim();
            HoaDonBanService.xuatHoaDon(soHoaDon);
        });

        btnTimkiem.addActionListener(e -> {
            String maKH = txtMakhachhang.getText().trim();
            if (!maKH.isEmpty()) {
                DefaultTableModel model = (DefaultTableModel) tblHoadonban.getModel();
                HoaDonBanService.timKiemHoaDonTheoMaKhachHang(model, maKH);
            } else {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập mã khách hàng để tìm kiếm.");
            }
        });

        // Thiết lập sự kiện cho JTable khi click vào một dòng
        tblHoadonban.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // Lấy chỉ số dòng được chọn
                int rowIndex = tblHoadonban.getSelectedRow();

                if (rowIndex >= 0) {
                    // Đổ dữ liệu vào các TextField từ các cột trong dòng được chọn
                    txtSohoadonban.setText(tblHoadonban.getValueAt(rowIndex, 0).toString()); // Số Hóa Đơn Bán
                    txtMaquanao.setText(tblHoadonban.getValueAt(rowIndex, 1).toString()); // Mã Quần Áo
                    txtMakhachhang.setText(tblHoadonban.getValueAt(rowIndex, 2).toString()); // Mã Khách Hàng
                    txtManhanvien.setText(tblHoadonban.getValueAt(rowIndex, 3).toString()); // Mã Nhân Viên
                    txtSoluongban.setText(tblHoadonban.getValueAt(rowIndex, 4).toString()); // Số Lượng Bán

                    // Lấy Mã Quần Áo
                    String maQuanAo = tblHoadonban.getValueAt(rowIndex, 1).toString();

                    // Truy vấn để lấy Tên Quần Áo từ bảng SanPham
                    String tenQuanAo =getTenQuanAoFromMaQuanAo(maQuanAo);
                    txtTenquanao.setText(tenQuanAo); // Đổ tên quần áo vào TextField
                }
            }
        });     
    }

    public String getTenQuanAoFromMaQuanAo(String maQuanAo) {
        String tenQuanAo = "";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Mở kết nối
            connection = ketnoiCSDL.getConnection();

            // Câu lệnh SQL để lấy tên quần áo từ MaQuanAo
            String query = "SELECT TenQuanAo FROM SanPham WHERE MaQuanAo = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, maQuanAo);

            // Thực thi truy vấn
            resultSet = statement.executeQuery();

            // Nếu có kết quả, lấy tên quần áo
            if (resultSet.next()) {
                tenQuanAo = resultSet.getString("TenQuanAo");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Xử lý lỗi nếu có
        } finally {
            // Đóng kết nối và các đối tượng liên quan
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tenQuanAo;
    }

    public void loadHoaDonBanToTable() {
        DefaultTableModel model = (DefaultTableModel) tblHoadonban.getModel();
        model.setRowCount(0); // xóa dữ liệu cũ

        String sql = "SELECT hdb.SoHoaDonBan, ct.MaQuanAo, hdb.MaKhachHang, hdb.MaNhanVien, ct.SoLuong, hdb.NgayBan, ct.ThanhTien " +
                    "FROM hoadonban hdb " +
                    "JOIN chitiethoadonban ct ON hdb.SoHoaDonBan = ct.SoHoaDonBan";

        try (Connection conn = ketnoiCSDL.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("SoHoaDonBan"),
                    rs.getInt("MaQuanAo"),
                    rs.getString("MaKhachHang"),
                    rs.getInt("MaNhanVien"),
                    rs.getInt("SoLuong"),
                    rs.getDate("NgayBan"),
                    rs.getDouble("ThanhTien")
                };
                model.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi tải dữ liệu hóa đơn: " + e.getMessage());
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
        tblHoadonban = new javax.swing.JTable();
        lblMakhachhang = new javax.swing.JLabel();
        lblSohoadonban = new javax.swing.JLabel();
        lblMaquanao = new javax.swing.JLabel();
        lblTenquanao = new javax.swing.JLabel();
        lblSoluongban = new javax.swing.JLabel();
        txtSohoadonban = new javax.swing.JTextField();
        txtMaquanao = new javax.swing.JTextField();
        txtTenquanao = new javax.swing.JTextField();
        txtSoluongban = new javax.swing.JTextField();
        txtMakhachhang = new javax.swing.JTextField();
        btnXuathoadon = new javax.swing.JButton();
        btnTimkiem = new javax.swing.JButton();
        txtManhanvien = new javax.swing.JTextField();
        lblManhanvien = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(980, 600));

        jPanel1.setBackground(new java.awt.Color(173, 216, 230));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("DANH SÁCH HÓA ĐƠN BÁN");

        tblHoadonban.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {}, // ban đầu rỗng, bạn sẽ đổ dữ liệu sau
            new String [] {
                "Số HĐ Bán", "Mã Quần Áo", "Mã KH", "Mã NV", "Số Lượng", "Ngày Bán", "Thành Tiền"
            }
        ));
        jScrollPane1.setViewportView(tblHoadonban);

        lblMakhachhang.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMakhachhang.setText("Mã khách hàng:");

        lblSohoadonban.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblSohoadonban.setText("Số hóa đơn bán:");

        lblMaquanao.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMaquanao.setText("Mã quần áo:");

        lblTenquanao.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblTenquanao.setText("Tên quần áo:");

        lblSoluongban.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblSoluongban.setText("Số lượng bán:");

        btnXuathoadon.setText("Xuất hóa đơn");
        btnXuathoadon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXuathoadonActionPerformed(evt);
            }
        });

        btnTimkiem.setText("Tìm kiếm");

        lblManhanvien.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblManhanvien.setText("Mã nhân viên:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 978, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblMakhachhang)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMakhachhang, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblSohoadonban)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSohoadonban, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblManhanvien)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtManhanvien, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnTimkiem)
                                .addGap(111, 111, 111)
                                .addComponent(lblMaquanao)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMaquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblSoluongban)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSoluongban, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblTenquanao)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTenquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(301, 301, 301)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(403, 403, 403)
                        .addComponent(btnXuathoadon, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1)
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMakhachhang)
                    .addComponent(lblMaquanao)
                    .addComponent(txtMaquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMakhachhang, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTimkiem, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblTenquanao)
                        .addComponent(txtTenquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtSohoadonban, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblSohoadonban)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblManhanvien)
                            .addComponent(txtManhanvien, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblSoluongban)
                            .addComponent(txtSoluongban, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnXuathoadon, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnXuathoadonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXuathoadonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnXuathoadonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnTimkiem;
    private javax.swing.JButton btnXuathoadon;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMakhachhang;
    private javax.swing.JLabel lblManhanvien;
    private javax.swing.JLabel lblMaquanao;
    private javax.swing.JLabel lblSohoadonban;
    private javax.swing.JLabel lblSoluongban;
    private javax.swing.JLabel lblTenquanao;
    private javax.swing.JTable tblHoadonban;
    private javax.swing.JTextField txtMakhachhang;
    private javax.swing.JTextField txtManhanvien;
    private javax.swing.JTextField txtMaquanao;
    private javax.swing.JTextField txtSohoadonban;
    private javax.swing.JTextField txtSoluongban;
    private javax.swing.JTextField txtTenquanao;
    // End of variables declaration//GEN-END:variables
}
