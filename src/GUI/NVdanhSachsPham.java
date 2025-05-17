/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.awt.Image;
import BackEnd.*;
import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Neo 16
 */
public class NVdanhsachsPham extends javax.swing.JPanel {

    /**
     * Creates new form NVdanhsachsPham1
     */
    public NVdanhsachsPham() {
        initComponents();
        doDuLieuSanPham();
        Sanphamdata.doDuLieuVaoComboBox(boxLoai, "theloai", "TenLoai");
        Sanphamdata.doDuLieuVaoComboBox(boxChatlieu, "chatlieu", "TenChatLieu");
        Sanphamdata.doDuLieuVaoComboBox(boxDoituong, "doituong", "TenDoiTuong");
        Sanphamdata.doDuLieuVaoComboBox(boxKichco, "co", "TenCo");
        Sanphamdata.doDuLieuVaoComboBox(boxMua, "mua", "TenMua");
        Sanphamdata.doDuLieuVaoComboBox(boxMau, "mau", "TenMau");
        Sanphamdata.doDuLieuVaoComboBox(boxNoisanxuat, "noisanxuat", "TenNSX");

        tblDsachSpham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                hienThiChiTietSanPham();
            }
        });
        
        btnLoc.addActionListener(e -> {
            String maquanao = txtMaquanao.getText().trim();
            String loai = boxLoai.getSelectedItem() != null ? boxLoai.getSelectedItem().toString() : null;
            String chatlieu = boxChatlieu.getSelectedItem() != null ? boxChatlieu.getSelectedItem().toString() : null;
            String doituong = boxDoituong.getSelectedItem() != null ? boxDoituong.getSelectedItem().toString() : null;
            String kichco = boxKichco.getSelectedItem() != null ? boxKichco.getSelectedItem().toString() : null;
            String mua = boxMua.getSelectedItem() != null ? boxMua.getSelectedItem().toString() : null;
            String mau = boxMau.getSelectedItem() != null ? boxMau.getSelectedItem().toString() : null;
            String nsx = boxNoisanxuat.getSelectedItem() != null ? boxNoisanxuat.getSelectedItem().toString() : null;
        
            Sanphamdata service = new Sanphamdata();
            DefaultTableModel model = service.locSanPham(maquanao, loai, chatlieu, doituong, kichco, mua, mau, nsx);
            tblDsachSpham.setModel(model);
        });

        btnLammoi.addActionListener(e -> {
            // Làm mới dữ liệu bảng sản phẩm
            doDuLieuSanPham();
        
            // Reset comboboxes
            boxLoai.setSelectedIndex(-1);
            boxChatlieu.setSelectedIndex(-1);
            boxDoituong.setSelectedIndex(-1);
            boxKichco.setSelectedIndex(-1);
            boxMua.setSelectedIndex(-1);
            boxMau.setSelectedIndex(-1);
            boxNoisanxuat.setSelectedIndex(-1);
        
            // Xóa dữ liệu các textboxes
            txtMaquanao.setText("");
            txtTenquanao.setText("");
            txtDongianhap.setText("");
            txtDongiaban.setText("");
            txtSoluong.setText("");
            txtAnh.setText("");
        
           hienThiAnh("src/GUI/icons/Ảnh chụp màn hình 2025-05-07 194832.png");  
        });
    }

    public void doDuLieuSanPham() {
        DefaultTableModel model = new DefaultTableModel(
            new String[] {
                "Mã quần áo", "Tên quần áo", "Thể loại", "Cỡ", "Chất liệu", 
                "Màu", "Đối tượng", "Mùa", "Nơi sản xuất", 
                "Đơn giá bán", "Đơn giá nhập", "Số lượng", "Ảnh"
            }, 0
        );
        tblDsachSpham.setModel(model);
    
        String sql = """
            SELECT 
                sp.MaQuanAo, sp.TenQuanAo, tl.TenLoai, c.TenCo, cl.TenChatLieu,
                m.TenMau, dt.TenDoiTuong, mu.TenMua, nsx.TenNSX,
                sp.DonGiaBan, sp.DonGiaNhap, sp.SoLuong, sp.Anh
            FROM sanpham sp
            JOIN theloai tl ON sp.MaLoai = tl.MaLoai
            JOIN co c ON sp.MaCo = c.MaCo
            JOIN chatlieu cl ON sp.MaChatLieu = cl.MaChatLieu
            JOIN mau m ON sp.MaMau = m.MaMau
            JOIN doituong dt ON sp.MaDoiTuong = dt.MaDoiTuong
            JOIN mua mu ON sp.MaMua = mu.MaMua
            JOIN noisanxuat nsx ON sp.MaNSX = nsx.MaNSX
        """;
    
        try (Connection conn = ketnoiCSDL.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
    
            while (rs.next()) {
                Object[] row = new Object[] {
                    rs.getInt("MaQuanAo"),
                    rs.getString("TenQuanAo"),
                    rs.getString("TenLoai"),
                    rs.getString("TenCo"),
                    rs.getString("TenChatLieu"),
                    rs.getString("TenMau"),
                    rs.getString("TenDoiTuong"),
                    rs.getString("TenMua"),
                    rs.getString("TenNSX"),
                    rs.getDouble("DonGiaBan"),
                    rs.getDouble("DonGiaNhap"),
                    rs.getInt("SoLuong"),
                    rs.getString("Anh") // Hiện đường dẫn hoặc tên file ảnh
                };
                model.addRow(row);
            }
    
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải dữ liệu sản phẩm:\n" + e.getMessage());
        }
    }

    public void hienThiChiTietSanPham() {
        int row = tblDsachSpham.getSelectedRow();
        if (row == -1) return; // không có dòng nào được chọn
    
        txtMaquanao.setText(tblDsachSpham.getValueAt(row, 0).toString());
        txtTenquanao.setText(tblDsachSpham.getValueAt(row, 1).toString());
        boxLoai.setSelectedItem(tblDsachSpham.getValueAt(row, 2).toString());
        boxKichco.setSelectedItem(tblDsachSpham.getValueAt(row, 3).toString());
        boxChatlieu.setSelectedItem(tblDsachSpham.getValueAt(row, 4).toString());
        boxMau.setSelectedItem(tblDsachSpham.getValueAt(row, 5).toString());
        boxDoituong.setSelectedItem(tblDsachSpham.getValueAt(row, 6).toString());
        boxMua.setSelectedItem(tblDsachSpham.getValueAt(row, 7).toString());
        boxNoisanxuat.setSelectedItem(tblDsachSpham.getValueAt(row, 8).toString());
        txtDongiaban.setText(tblDsachSpham.getValueAt(row, 9).toString());
        txtDongianhap.setText(tblDsachSpham.getValueAt(row, 10).toString());
        txtSoluong.setText(tblDsachSpham.getValueAt(row, 11).toString());
        txtAnh.setText(tblDsachSpham.getValueAt(row, 12).toString());
    
        // Gọi hàm hiển thị ảnh
        hienThiAnh(tblDsachSpham.getValueAt(row, 12).toString());
    }
    public void hienThiAnh(String duongDan) {
        try {
            // Đảm bảo đường dẫn hợp lệ (kiểm tra xem tệp có tồn tại không)
            File file = new File(duongDan);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null, "Ảnh không tồn tại tại: " + duongDan);
                return;
            }
    
            // Tạo đối tượng ImageIcon từ đường dẫn ảnh
            ImageIcon icon = new ImageIcon(duongDan);
            // Tải ảnh và thay đổi kích thước phù hợp với JPanel (Anh)
            Image img = icon.getImage().getScaledInstance(Anh.getWidth(), Anh.getHeight(), Image.SCALE_SMOOTH);
    
            // Tạo một JLabel chứa ảnh đã thay đổi kích thước
            JLabel lbl = new JLabel(new ImageIcon(img));
    
            // Xóa hết nội dung cũ của JPanel trước khi thêm ảnh mới
            Anh.removeAll();
            Anh.setLayout(new BorderLayout()); // Đảm bảo layout hợp lý
            Anh.add(lbl, BorderLayout.CENTER); // Thêm JLabel vào giữa JPanel
    
            // Cập nhật lại giao diện
            Anh.revalidate();
            Anh.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Không thể hiển thị ảnh: " + duongDan);
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
        tblDsachSpham = new javax.swing.JTable();
        lblMaquanao = new javax.swing.JLabel();
        lblTenquanao = new javax.swing.JLabel();
        txtTenquanao = new javax.swing.JTextField();
        txtMaquanao = new javax.swing.JTextField();
        txtDongianhap = new javax.swing.JTextField();
        lblDongianhap = new javax.swing.JLabel();
        boxLoai = new javax.swing.JComboBox<>();
        lblLoai = new javax.swing.JLabel();
        boxChatlieu = new javax.swing.JComboBox<>();
        lblChatlieu = new javax.swing.JLabel();
        boxDoituong = new javax.swing.JComboBox<>();
        lblDoituong = new javax.swing.JLabel();
        boxKichco = new javax.swing.JComboBox<>();
        lblKichco = new javax.swing.JLabel();
        boxMua = new javax.swing.JComboBox<>();
        lblMua = new javax.swing.JLabel();
        boxMau = new javax.swing.JComboBox<>();
        lblMau = new javax.swing.JLabel();
        boxNoisanxuat = new javax.swing.JComboBox<>();
        lblNoisanxuat = new javax.swing.JLabel();
        txtDongiaban = new javax.swing.JTextField();
        lblDongiaban = new javax.swing.JLabel();
        txtSoluong = new javax.swing.JTextField();
        lblSoluong = new javax.swing.JLabel();
        Anh = new javax.swing.JPanel();
        btnLammoi = new javax.swing.JButton();
        btnLoc = new javax.swing.JButton();
        txtAnh = new javax.swing.JTextField();

        jPanel1.setBackground(new java.awt.Color(173, 216, 230));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("DANH SÁCH SẢN PHẨM");

        tblDsachSpham.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblDsachSpham);

        lblMaquanao.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMaquanao.setText("Mã quần áo:");

        lblTenquanao.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblTenquanao.setText("Tên quần áo:");

        lblDongianhap.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblDongianhap.setText("Đơn giá nhập:");

        boxLoai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        boxLoai.setPreferredSize(new java.awt.Dimension(100, 28));

        lblLoai.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblLoai.setText("Loại:");

        boxChatlieu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        boxChatlieu.setPreferredSize(new java.awt.Dimension(100, 28));

        lblChatlieu.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblChatlieu.setText("Chất liệu:");

        boxDoituong.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        boxDoituong.setPreferredSize(new java.awt.Dimension(100, 28));

        lblDoituong.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblDoituong.setText("Đối tượng:");

        boxKichco.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        boxKichco.setPreferredSize(new java.awt.Dimension(100, 28));

        lblKichco.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblKichco.setText("Kích cỡ:");

        boxMua.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        boxMua.setPreferredSize(new java.awt.Dimension(100, 28));

        lblMua.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMua.setText("Mùa:");

        boxMau.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        boxMau.setPreferredSize(new java.awt.Dimension(100, 28));

        lblMau.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMau.setText("Màu:");

        boxNoisanxuat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        boxNoisanxuat.setPreferredSize(new java.awt.Dimension(100, 28));

        lblNoisanxuat.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblNoisanxuat.setText("Nơi sản xuất:");

        lblDongiaban.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblDongiaban.setText("Đơn giá bán:");

        lblSoluong.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblSoluong.setText("Số lượng:");

        javax.swing.GroupLayout AnhLayout = new javax.swing.GroupLayout(Anh);
        Anh.setLayout(AnhLayout);
        AnhLayout.setHorizontalGroup(
            AnhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 142, Short.MAX_VALUE)
        );
        AnhLayout.setVerticalGroup(
            AnhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 201, Short.MAX_VALUE)
        );

        btnLammoi.setText("Làm mới");

        btnLoc.setText("Lọc");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 967, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(327, 327, 327)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGap(46, 46, 46)
                                            .addComponent(lblMaquanao)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtMaquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(96, 96, 96)
                                            .addComponent(lblTenquanao)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtTenquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGap(60, 60, 60)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(lblChatlieu)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(boxChatlieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(lblDoituong)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(boxDoituong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGap(119, 119, 119)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(lblMua)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(boxMua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGap(18, 18, 18)
                                                    .addComponent(lblNoisanxuat)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(boxNoisanxuat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(lblMau)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(boxMau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                    .addGap(18, 18, 18))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(110, 110, 110)
                                    .addComponent(lblLoai)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(boxLoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(105, 105, 105)
                                    .addComponent(lblKichco)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(boxKichco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(286, 286, 286)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addComponent(lblDongianhap)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDongianhap, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblDongiaban)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDongiaban, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblSoluong)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSoluong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(42, 42, 42)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(txtAnh, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(Anh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(426, 426, 426)
                .addComponent(btnLammoi, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(Anh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtAnh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(102, 102, 102)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(boxKichco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblKichco)
                                            .addComponent(boxMau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblMau))
                                        .addGap(87, 87, 87))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(boxMua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblMua)
                                            .addComponent(boxNoisanxuat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblNoisanxuat)
                                            .addComponent(boxDoituong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblDoituong))
                                        .addGap(33, 33, 33)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblDongianhap)
                                    .addComponent(txtDongianhap, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblDongiaban)
                                    .addComponent(txtDongiaban, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblSoluong)
                                    .addComponent(txtSoluong, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblMaquanao)
                                    .addComponent(lblTenquanao)
                                    .addComponent(txtTenquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtMaquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(boxLoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblLoai))
                                .addGap(14, 14, 14)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(boxChatlieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblChatlieu))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLammoi, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
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
    private javax.swing.JPanel Anh;
    private javax.swing.JComboBox<String> boxChatlieu;
    private javax.swing.JComboBox<String> boxDoituong;
    private javax.swing.JComboBox<String> boxKichco;
    private javax.swing.JComboBox<String> boxLoai;
    private javax.swing.JComboBox<String> boxMau;
    private javax.swing.JComboBox<String> boxMua;
    private javax.swing.JComboBox<String> boxNoisanxuat;
    private javax.swing.JButton btnLammoi;
    private javax.swing.JButton btnLoc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblChatlieu;
    private javax.swing.JLabel lblDoituong;
    private javax.swing.JLabel lblDongiaban;
    private javax.swing.JLabel lblDongianhap;
    private javax.swing.JLabel lblKichco;
    private javax.swing.JLabel lblLoai;
    private javax.swing.JLabel lblMaquanao;
    private javax.swing.JLabel lblMau;
    private javax.swing.JLabel lblMua;
    private javax.swing.JLabel lblNoisanxuat;
    private javax.swing.JLabel lblSoluong;
    private javax.swing.JLabel lblTenquanao;
    private javax.swing.JTable tblDsachSpham;
    private javax.swing.JTextField txtAnh;
    private javax.swing.JTextField txtDongiaban;
    private javax.swing.JTextField txtDongianhap;
    private javax.swing.JTextField txtMaquanao;
    private javax.swing.JTextField txtSoluong;
    private javax.swing.JTextField txtTenquanao;
    // End of variables declaration//GEN-END:variables
}
