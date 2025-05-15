/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.File;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import BackEnd.*;
/**
 *
 * @author Neo 16
 */
public class Nhanvienlaphoadon extends javax.swing.JPanel {

    /**
     * Creates new form Nhanvienlaphoadon1
     */
    public Nhanvienlaphoadon() {
        initComponents();
        loadDuLieuGioHang();

        tblLaphoadon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tblLaphoadon.getSelectedRow();
                if (row >= 0) {
                    txtMakhachhang.setText(tblLaphoadon.getValueAt(row, 0).toString());
                    txtMaquanao.setText(tblLaphoadon.getValueAt(row, 1).toString());
                    txtDongiaban.setText(tblLaphoadon.getValueAt(row, 2).toString());
                    txtSoluong.setText(tblLaphoadon.getValueAt(row, 3).toString());
                    txtTongtien.setText(tblLaphoadon.getValueAt(row, 4).toString());

                    Object imgObj = tblLaphoadon.getValueAt(row, 5);
                    if (imgObj instanceof ImageIcon) {
                        showImageOnPanel((ImageIcon) imgObj);
                    } else {
                        clearImagePanel();
                    }
                }
            }
        });
        btnTimkiem.addActionListener(e -> {
            String maKH = txtMakhachhang.getText().trim();
            DefaultTableModel model = DonHangDAO.timKiemTrongGioHang(maKH);
            tblLaphoadon.setModel(model);
        });

        btnLammoi.addActionListener(e -> {
            // 1. Xóa trắng các textbox
            txtMakhachhang.setText("");
            txtMaquanao.setText("");
            txtDongiaban.setText("");
            txtTongtien.setText("");
            txtSoluong.setText("");

            // 2. Xóa ảnh hiển thị (nếu có Panel ảnh riêng)
            Anh.removeAll();
            Anh.repaint();

            // 3. Tải lại dữ liệu bảng GioHang
            loadDuLieuGioHang();
        });

        btnLaphoadon.addActionListener(e -> {
            int row = tblLaphoadon.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn dòng!");
                return;
            }

            String maKH = tblLaphoadon.getValueAt(row, 0).toString();
            String maQA = tblLaphoadon.getValueAt(row, 1).toString();
            double donGia = Double.parseDouble(tblLaphoadon.getValueAt(row, 2).toString());
            int soLuong = Integer.parseInt(tblLaphoadon.getValueAt(row, 3).toString());

            // Giả sử bạn có mã nhân viên đang đăng nhập như sau:
            String maNV = SessionManager.getMaTaiKhoan();

            DonHangDAO.lapHoaDon(maQA, soLuong, donGia, maKH, maNV, tblLaphoadon);
            loadDuLieuGioHang(); // Sau khi lập hóa đơn xong thì reload lại bảng
        });
    }

    private void showImageOnPanel(ImageIcon icon) {
        Anh.removeAll(); // clear ảnh cũ

        // Resize ảnh lớn hơn cho panel
        Image img = icon.getImage().getScaledInstance(200, 270, Image.SCALE_SMOOTH);
        JLabel lbl = new JLabel(new ImageIcon(img));

        Anh.setLayout(new BorderLayout());
        Anh.add(lbl, BorderLayout.CENTER);
        Anh.revalidate();
        Anh.repaint();
    }

    private void clearImagePanel() {
        Anh.removeAll();
        Anh.repaint();
    }

    public void loadDuLieuGioHang() {
        String[] columnNames = {"Mã Khách hàng", "Mã Quần Áo", "Đơn Giá Bán", "Số Lượng Đặt", "Tổng Tiền", "Ảnh"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) return ImageIcon.class; // Cột Ảnh
                return Object.class;
            }
        };

        try (Connection conn = ketnoiCSDL.getConnection()) {
            String sql = "SELECT g.MaKhachHang, g.MaQuanAo, g.DonGiaBan, g.SoLuongDat, g.TongTien, s.Anh " +
                        "FROM GioHang g JOIN SanPham s ON g.MaQuanAo = s.MaQuanAo";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String maKH = rs.getString("MaKhachHang");
                String maQA = rs.getString("MaQuanAo");
                double donGia = rs.getDouble("DonGiaBan");
                int soLuong = rs.getInt("SoLuongDat");
                double tongTien = rs.getDouble("TongTien");
                String tenFileAnh = rs.getString("Anh");

                // Load ảnh từ thư mục, ví dụ: /images/
                ImageIcon icon = null;
                if (tenFileAnh != null) {
                    File imgFile = new File(tenFileAnh); // bạn chỉnh lại đường dẫn theo dự án
                    if (imgFile.exists()) {
                        Image img = new ImageIcon(imgFile.getAbsolutePath()).getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(img);
                    }
                }

                Object[] row = {maKH, maQA, donGia, soLuong, tongTien, icon};
                model.addRow(row);
            }

            tblLaphoadon.setRowHeight(60); // Đặt chiều cao hàng phù hợp với ảnh
            tblLaphoadon.setModel(model);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Lỗi tải dữ liệu giỏ hàng: " + ex.getMessage());
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
        tblLaphoadon = new javax.swing.JTable();
        lblMaquanao = new javax.swing.JLabel();
        txtMakhachhang = new javax.swing.JTextField();
        txtMaquanao = new javax.swing.JTextField();
        lblTenquanao = new javax.swing.JLabel();
        btnTimkiem = new javax.swing.JButton();
        Anh = new javax.swing.JPanel();
        lblMau = new javax.swing.JLabel();
        txtDongiaban = new javax.swing.JTextField();
        lblDongiaban = new javax.swing.JLabel();
        btnLaphoadon = new javax.swing.JButton();
        btnLammoi = new javax.swing.JButton();
        txtTongtien = new javax.swing.JTextField();
        lblDongiaban1 = new javax.swing.JLabel();
        txtSoluong = new javax.swing.JTextField();

        jPanel1.setBackground(new java.awt.Color(173, 216, 230));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("LẬP HÓA ĐƠN");

        tblLaphoadon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblLaphoadon);

        lblMaquanao.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMaquanao.setText("Mã khách hàng:");

        lblTenquanao.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblTenquanao.setText("Mã quần áo");

        btnTimkiem.setText("Tìm kiếm");
        btnTimkiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimkiemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout AnhLayout = new javax.swing.GroupLayout(Anh);
        Anh.setLayout(AnhLayout);
        AnhLayout.setHorizontalGroup(
            AnhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 165, Short.MAX_VALUE)
        );
        AnhLayout.setVerticalGroup(
            AnhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 254, Short.MAX_VALUE)
        );

        lblMau.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMau.setText("Số lượng:");

        lblDongiaban.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblDongiaban.setText("Đơn giá bán:");

        btnLaphoadon.setText("Lập hóa đơn");

        btnLammoi.setText("Làm mới");

        lblDongiaban1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblDongiaban1.setText("Tổng tiền:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblMaquanao)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMakhachhang, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblMau)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSoluong, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblDongiaban)
                                    .addComponent(lblTenquanao))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtMaquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDongiaban, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(79, 79, 79)
                                .addComponent(lblDongiaban1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTongtien, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(btnTimkiem)))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addGap(200, 200, 200)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(Anh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 19, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLaphoadon)
                .addGap(199, 199, 199)
                .addComponent(btnLammoi, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(313, 313, 313))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblMaquanao)
                            .addComponent(txtMakhachhang, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnTimkiem, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTenquanao)
                            .addComponent(txtMaquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblDongiaban)
                            .addComponent(txtDongiaban, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblMau)
                            .addComponent(txtSoluong, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDongiaban1)
                            .addComponent(txtTongtien, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(Anh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnLammoi, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(btnLaphoadon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(107, 107, 107))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 601, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnTimkiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimkiemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTimkiemActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Anh;
    private javax.swing.JButton btnLammoi;
    private javax.swing.JButton btnLaphoadon;
    private javax.swing.JButton btnTimkiem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDongiaban;
    private javax.swing.JLabel lblDongiaban1;
    private javax.swing.JLabel lblMaquanao;
    private javax.swing.JLabel lblMau;
    private javax.swing.JLabel lblTenquanao;
    private javax.swing.JTable tblLaphoadon;
    private javax.swing.JTextField txtDongiaban;
    private javax.swing.JTextField txtMakhachhang;
    private javax.swing.JTextField txtMaquanao;
    private javax.swing.JTextField txtSoluong;
    private javax.swing.JTextField txtTongtien;
    // End of variables declaration//GEN-END:variables
}
