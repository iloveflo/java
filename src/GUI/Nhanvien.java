/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI;


import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import BackEnd.NhanvienData;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/**
 *
 * @author Neo 16
 */
public class Nhanvien extends javax.swing.JPanel {

    /**
     * Creates new form Nhanvien1
     */
    public Nhanvien() {
        initComponents();
        loadNhanvienTable();
        tblNhanvien.getSelectionModel().addListSelectionListener(e -> {
            int row = tblNhanvien.getSelectedRow();
            if (row >= 0) {
                txtManhanvien.setText(tblNhanvien.getValueAt(row, 0).toString());
                txtHoten.setText(tblNhanvien.getValueAt(row, 1).toString());
                boxGioitinh.setSelectedItem(tblNhanvien.getValueAt(row, 2).toString());
                txtNgaysinh.setText(tblNhanvien.getValueAt(row, 3).toString());
                txtSDT.setText(tblNhanvien.getValueAt(row, 4).toString());
                txtDiachi.setText(tblNhanvien.getValueAt(row, 5).toString());
                txtEmail.setText(tblNhanvien.getValueAt(row, 6).toString());
                txtCongviec.setText(tblNhanvien.getValueAt(row, 7).toString());
            }
        });

        btnLammoi.addActionListener(e -> {
            loadNhanvienTable();
            clearTextFields();
        });
        btnThem.addActionListener(e -> {
            NhanvienData.themNhanVien(
                txtManhanvien.getText(),
                txtHoten.getText(),
                boxGioitinh.getSelectedItem().toString(),
                txtNgaysinh.getText(),
                txtSDT.getText(),
                txtDiachi.getText(),
                txtEmail.getText(),
                txtCongviec.getText()
            );
        });
        btnXoa.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                int selectedRow = tblNhanvien.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn nhân viên cần xóa!");
                    return;
                }
        
                String maNV = tblNhanvien.getValueAt(selectedRow, 0).toString();
        
                int confirm = JOptionPane.showConfirmDialog(null,
                        "Bạn có chắc muốn xóa nhân viên mã " + maNV + " không?",
                        "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean result = NhanvienData.xoaNhanVien(maNV);
                    if (result) {
                        JOptionPane.showMessageDialog(null, "Xóa nhân viên thành công!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Xóa nhân viên thất bại!");
                    }
                }
            }
        });
        btnTimkiem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String maNV = txtManhanvien.getText().trim();
        
                if (maNV.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Vui lòng nhập mã nhân viên cần tìm!");
                    return;
                }
        
                List<Object[]> ketQua = NhanvienData.timKiemNhanVienTheoMa(maNV);
                DefaultTableModel model = (DefaultTableModel) tblNhanvien.getModel();
                model.setRowCount(0); // Xóa bảng cũ
        
                if (ketQua.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Không tìm thấy nhân viên với mã: " + maNV);
                } else {
                    for (Object[] row : ketQua) {
                        model.addRow(row);
                    }
                }
            }
        });
        
        btnCapnhat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String maNV = txtManhanvien.getText().trim();
                String hoTen = txtHoten.getText().trim();
                String gioiTinh = boxGioitinh.getSelectedItem().toString();
                String ngaySinh = txtNgaysinh.getText().trim();
                String sdt = txtSDT.getText().trim();
                String diaChi = txtDiachi.getText().trim();
                String email = txtEmail.getText().trim();
                String maCongViec = txtCongviec.getText().trim();
        
                boolean result = NhanvienData.capNhatNhanVien(maNV, hoTen, gioiTinh, ngaySinh, sdt, diaChi, email, maCongViec);
                if (result) {
                    JOptionPane.showMessageDialog(null, "Cập nhật nhân viên thành công!");
                    // Load lại bảng nếu cần
                } else {
                    JOptionPane.showMessageDialog(null, "Cập nhật nhân viên thất bại!");
                }
            }
        });        
    }

    private void clearTextFields() {
        txtManhanvien.setText("");
        txtHoten.setText("");
        txtNgaysinh.setText("");
        txtSDT.setText("");
        txtDiachi.setText("");
        txtEmail.setText("");
        txtCongviec.setText(""); // nếu dùng TextField
        boxGioitinh.setSelectedIndex(0); // hoặc set lại về "Nam"
    } 
    private void loadNhanvienTable() {
        DefaultTableModel model = (DefaultTableModel) tblNhanvien.getModel();
        model.setRowCount(0); // clear
        List<NhanvienData.Nhanvien> list = NhanvienData.getAllNhanVien();

        for (NhanvienData.Nhanvien nv : list) {
            model.addRow(new Object[]{
                nv.getMaNhanVien(),
                nv.getTenNhanVien(),
                nv.getGioiTinh(),
                nv.getNgaySinh(),
                nv.getSoDienThoai(),
                nv.getDiaChi(),
                nv.getEmail(),
                nv.getTenCongViec()
            });
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
        tblNhanvien = new javax.swing.JTable();
        lblManhanvien = new javax.swing.JLabel();
        lblDiachi = new javax.swing.JLabel();
        lblHoten = new javax.swing.JLabel();
        lblNgaysinh = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        txtDiachi = new javax.swing.JTextField();
        txtHoten = new javax.swing.JTextField();
        txtNgaysinh = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtManhanvien = new javax.swing.JTextField();
        btnXoa = new javax.swing.JButton();
        btnTimkiem = new javax.swing.JButton();
        btnLammoi = new javax.swing.JButton();
        btnCapnhat = new javax.swing.JButton();
        txtSDT = new javax.swing.JTextField();
        lblSDT = new javax.swing.JLabel();
        boxGioitinh = new javax.swing.JComboBox<>();
        lblGioitinh = new javax.swing.JLabel();
        txtCongviec = new javax.swing.JTextField();
        lblCongviec = new javax.swing.JLabel();
        btnThem = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(200, 173, 127));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("DANH SÁCH NHÂN VIÊN");

        tblNhanvien.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][] {}, // không có dữ liệu sẵn
            new String[] {
                "Mã NV", "Họ tên", "Giới tính", "Ngày sinh", "SĐT", "Địa chỉ", "Email", "Công việc"
            }
        ));
        jScrollPane1.setViewportView(tblNhanvien);

        lblManhanvien.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblManhanvien.setText("Mã nhân viên:");

        lblDiachi.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblDiachi.setText("Địa chỉ:");

        lblHoten.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblHoten.setText("Họ tên:");

        lblNgaysinh.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblNgaysinh.setText("Ngày sinh:");

        lblEmail.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblEmail.setText("Email:");

        btnXoa.setText("Xóa");

        btnTimkiem.setText("Tìm kiếm");

        btnLammoi.setText("Làm mới");

        btnCapnhat.setText("Cập nhật");

        lblSDT.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblSDT.setText("Số điện thoại:");

        boxGioitinh.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nam", "Nữ", "Khác" }));

        lblGioitinh.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblGioitinh.setText("Giới tính:");

        lblCongviec.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblCongviec.setText("Công việc:");

        btnThem.setText("Thêm");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblManhanvien)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtManhanvien, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblSDT)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblDiachi)
                                    .addComponent(lblGioitinh))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(boxGioitinh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDiachi, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnTimkiem)
                                .addGap(90, 90, 90)
                                .addComponent(lblHoten)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtHoten, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(lblNgaysinh)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtNgaysinh, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addGap(1, 1, 1)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                            .addComponent(lblEmail)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                            .addComponent(lblCongviec)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtCongviec, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(312, 312, 312)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(131, 131, 131)
                        .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(65, 65, 65)
                        .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(88, 88, 88)
                        .addComponent(btnCapnhat, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(85, 85, 85)
                        .addComponent(btnLammoi, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(195, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel1)
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblManhanvien)
                    .addComponent(lblHoten)
                    .addComponent(txtHoten, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtManhanvien, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTimkiem, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblSDT)
                            .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(32, 32, 32)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDiachi, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDiachi))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(boxGioitinh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblGioitinh)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNgaysinh)
                            .addComponent(txtNgaysinh, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(32, 32, 32)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblEmail)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCongviec, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCongviec))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCapnhat, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLammoi, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> boxGioitinh;
    private javax.swing.JButton btnCapnhat;
    private javax.swing.JButton btnLammoi;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnTimkiem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCongviec;
    private javax.swing.JLabel lblDiachi;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblGioitinh;
    private javax.swing.JLabel lblHoten;
    private javax.swing.JLabel lblManhanvien;
    private javax.swing.JLabel lblNgaysinh;
    private javax.swing.JLabel lblSDT;
    private javax.swing.JTable tblNhanvien;
    private javax.swing.JTextField txtCongviec;
    private javax.swing.JTextField txtDiachi;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtHoten;
    private javax.swing.JTextField txtManhanvien;
    private javax.swing.JTextField txtNgaysinh;
    private javax.swing.JTextField txtSDT;
    // End of variables declaration//GEN-END:variables
}
