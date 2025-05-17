package GUI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import BackEnd.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class HoadonnhapNV extends javax.swing.JPanel {

    private javax.swing.JComboBox<String> boxNhacungcap;
    private javax.swing.JButton btnTimkiem;
    private javax.swing.JButton btnXuathoadon;
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
    private javax.swing.JButton btnLaphoadon;

    public HoadonnhapNV(){
        initComponents();
        loadNhaCungCap();
        loadHoaDonNhapData();

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

        btnTimkiem.addActionListener(e -> {
            String soHD = txtSohoadonnhap.getText().trim();
            if (!soHD.isEmpty()) {
                DefaultTableModel model = HoaDonNhapService.timKiemHoaDonNhap(soHD);
                tblHoadonnhap.setModel(model);
            } else {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập số hóa đơn nhập cần tìm.");
            }
        });

        btnXuathoadon.addActionListener(e -> {
            int selectedRow = tblHoadonnhap.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn một hóa đơn để xuất.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String soHoaDonNhap = tblHoadonnhap.getValueAt(selectedRow, 0).toString();
            HoaDonNhapService.xuat(soHoaDonNhap);
        });

        btnLaphoadon.addActionListener(e -> {
            HoaDonNhapService.lapHoaDonNhap(
                txtMaquanao.getText(),
                txtDongia.getText(),
                txtGiamgia.getText(),
                txtSoluongnhap.getText(),
                txtManhanvien.getText(),
                boxNhacungcap.getSelectedItem().toString()
            );
        });
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
        btnXuathoadon = new javax.swing.JButton();
        btnLaphoadon = new javax.swing.JButton();
        btnTimkiem = new javax.swing.JButton();
        txtManhanvien = new javax.swing.JTextField();
        lblManhanvien = new javax.swing.JLabel();
        lblSohoadonnhap1 = new javax.swing.JLabel();
        boxNhacungcap = new javax.swing.JComboBox<>();

        jPanel1.setBackground(new java.awt.Color(173, 216, 230));

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

        btnXuathoadon.setText("Xuất hóa đơn");
        jPanel1.add(btnXuathoadon);
        btnXuathoadon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXuathoadonActionPerformed(evt);
            }
        });

        btnLaphoadon.setText("Lập hóa đơn");
        jPanel1.add(btnLaphoadon);
        btnLaphoadon.setBounds(250, 515, 100, 40);


        btnTimkiem.setText("Tìm kiếm");

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
                .addGap(48, 48, 48)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblSohoadonnhap1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSohoadonnhap, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblManhanvien)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtManhanvien, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblMaquanao)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMaquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblNhacungcap)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(boxNhacungcap, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(32, 32, 32)
                .addComponent(btnTimkiem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDongia, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblGiamgia, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblSoluongnhap, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtSoluongnhap, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .addComponent(txtGiamgia)
                    .addComponent(txtDongia))
                .addGap(125, 125, 125))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(298, 298, 298)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 832, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(400, 400, 400)
                        .addComponent(btnXuathoadon, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(100, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtSohoadonnhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnTimkiem, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblSohoadonnhap1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(9, 9, 9))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblDongia)
                        .addComponent(txtDongia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMaquanao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblMaquanao))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblManhanvien)
                            .addComponent(txtManhanvien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNhacungcap)
                            .addComponent(boxNhacungcap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblGiamgia)
                            .addComponent(txtGiamgia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblSoluongnhap)
                            .addComponent(txtSoluongnhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnXuathoadon, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
            
        );
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 5, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnXuathoadonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXuathoadonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnXuathoadonActionPerformed

}
