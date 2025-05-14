/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import BackEnd.*;

/**
 *
 * @author Neo 16
 */
public class Sanpham extends javax.swing.JPanel {

    /**
     * Creates new form Sanpham1
     */
    public Sanpham() {
        initComponents();
        doDuLieuSanPham();
        Sanphamdata.doDuLieuVaoComboBox(boxLoai, "theloai", "TenLoai");
        Sanphamdata.doDuLieuVaoComboBox(boxChatlieu, "chatlieu", "TenChatLieu");
        Sanphamdata.doDuLieuVaoComboBox(boxDoituong, "doituong", "TenDoiTuong");
        Sanphamdata.doDuLieuVaoComboBox(boxKichco, "co", "TenCo");
        Sanphamdata.doDuLieuVaoComboBox(boxMua, "mua", "TenMua");
        Sanphamdata.doDuLieuVaoComboBox(boxMau, "mau", "TenMau");
        Sanphamdata.doDuLieuVaoComboBox(boxNoisanxuat, "noisanxuat", "TenNSX");
        tblSanpham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                hienThiChiTietSanPham();
            }
        }); 

        btnThem.addActionListener(e -> {
            String tenQuanAo = txtTenquanao.getText();
            String maQuanAo = txtMaquanao.getText();
            String donGiaNhap = txtDongianhap.getText();
            String donGiaBan = txtDongiaban.getText();
            String soLuong = txtSoluong.getText();
            String anh = txtAnh.getText();
        
            // Call the backend method to add the product
            boolean result = Sanphamdata.themSanPham(tenQuanAo, maQuanAo, donGiaNhap, donGiaBan, soLuong, anh, 
                                                     boxLoai, boxChatlieu, boxDoituong, boxKichco, boxMua, boxMau, boxNoisanxuat);
            if (result) {
                doDuLieuSanPham();
            }
        });
        btnXoa.addActionListener(e -> {
            String maQuanAo = txtMaquanao.getText().trim();
        
            if (maQuanAo.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập mã quần áo cần xóa!");
                return;
            }
        
            // Hộp thoại xác nhận
            int confirm = JOptionPane.showConfirmDialog(null,
                "Bạn có chắc chắn muốn xóa sản phẩm có mã: " + maQuanAo + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
        
            // Nếu người dùng chọn YES thì tiến hành xóa
            if (confirm == JOptionPane.YES_OPTION) {
                boolean xoaThanhCong = Sanphamdata.xoaSanPham(maQuanAo);
        
                if (xoaThanhCong) {
                    doDuLieuSanPham();
                }
            }
        });
        btnCapnhat.addActionListener(e -> {
            String maQuanAo = txtMaquanao.getText().trim();
            String tenQuanAo = txtTenquanao.getText().trim();
            String soLuongStr = txtSoluong.getText().trim();
            String donGiaNhapStr = txtDongianhap.getText().trim();
            String donGiaBanStr = txtDongiaban.getText().trim();
            String anh = txtAnh.getText().trim();
        
            // Kiểm tra rỗng
            if (maQuanAo.isEmpty() || tenQuanAo.isEmpty() || soLuongStr.isEmpty() ||
                donGiaNhapStr.isEmpty() || donGiaBanStr.isEmpty() || anh.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin sản phẩm!");
                return;
            }
            
            try {
                int soLuong = Integer.parseInt(soLuongStr);
                double donGiaNhap = Double.parseDouble(donGiaNhapStr);
                double donGiaBan = Double.parseDouble(donGiaBanStr);
        
                // Lấy ID từ combobox
                int maLoai = Sanphamdata.getIdFromCombo(boxLoai,"MaLoai", "theloai", "TenLoai");
                int maChatLieu = Sanphamdata.getIdFromCombo(boxChatlieu,"MaChatLieu", "chatlieu", "TenChatLieu");
                int maDoituong = Sanphamdata.getIdFromCombo(boxDoituong,"MaDoiTuong", "doituong", "TenDoiTuong");
                int maKichCo = Sanphamdata.getIdFromCombo(boxKichco,"MaCo", "co", "TenCo");
                int maMua = Sanphamdata.getIdFromCombo(boxMua,"MaMua", "mua", "TenMua");
                int maMau = Sanphamdata.getIdFromCombo(boxMau,"MaMau", "mau", "TenMau");
                int maNSX = Sanphamdata.getIdFromCombo(boxNoisanxuat,"MaNSX", "noisanxuat", "TenNSX");
        
                // Gọi DAO cập nhật
                boolean thanhCong = Sanphamdata.capNhatSanPham(
                    maQuanAo, tenQuanAo, maLoai, maKichCo, maChatLieu,
                    maMau, maDoituong, maMua, maNSX, soLuong,
                    anh, donGiaNhap, donGiaBan
                );
        
                if (thanhCong) {
                    doDuLieuSanPham();
                }
        
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Đơn giá hoặc số lượng không hợp lệ!");
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
            tblSanpham.setModel(model);
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
        btnMo.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image files", "jpg", "jpeg", "png"
            );
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();
                txtAnh.setText(filePath);  // Hiển thị đường dẫn

                // Hiển thị ảnh vào JPanel "Anh"
                hienThiAnh(filePath);     // Gọi lại hàm bạn đã có sẵn
            }
        });                                           
    }

    public void hienThiChiTietSanPham() {
        int row = tblSanpham.getSelectedRow();
        if (row == -1) return; // không có dòng nào được chọn
    
        txtMaquanao.setText(tblSanpham.getValueAt(row, 0).toString());
        txtTenquanao.setText(tblSanpham.getValueAt(row, 1).toString());
        boxLoai.setSelectedItem(tblSanpham.getValueAt(row, 2).toString());
        boxKichco.setSelectedItem(tblSanpham.getValueAt(row, 3).toString());
        boxChatlieu.setSelectedItem(tblSanpham.getValueAt(row, 4).toString());
        boxMau.setSelectedItem(tblSanpham.getValueAt(row, 5).toString());
        boxDoituong.setSelectedItem(tblSanpham.getValueAt(row, 6).toString());
        boxMua.setSelectedItem(tblSanpham.getValueAt(row, 7).toString());
        boxNoisanxuat.setSelectedItem(tblSanpham.getValueAt(row, 8).toString());
        txtDongiaban.setText(tblSanpham.getValueAt(row, 9).toString());
        txtDongianhap.setText(tblSanpham.getValueAt(row, 10).toString());
        txtSoluong.setText(tblSanpham.getValueAt(row, 11).toString());
        txtAnh.setText(tblSanpham.getValueAt(row, 12).toString());
    
        // Gọi hàm hiển thị ảnh
        hienThiAnh(tblSanpham.getValueAt(row, 12).toString());
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
    
    public void doDuLieuSanPham() {
        DefaultTableModel model = new DefaultTableModel(
            new String[] {
                "Mã quần áo", "Tên quần áo", "Thể loại", "Cỡ", "Chất liệu", 
                "Màu", "Đối tượng", "Mùa", "Nơi sản xuất", 
                "Đơn giá bán", "Đơn giá nhập", "Số lượng", "Ảnh"
            }, 0
        );
        tblSanpham.setModel(model);
    
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
        tblSanpham = new javax.swing.JTable();
        lblMaquanao = new javax.swing.JLabel();
        lblTenquanao = new javax.swing.JLabel();
        txtTenquanao = new javax.swing.JTextField();
        txtMaquanao = new javax.swing.JTextField();
        btnXoa = new javax.swing.JButton();
        btnCapnhat = new javax.swing.JButton();
        txtDongianhap = new javax.swing.JTextField();
        lblDongianhap = new javax.swing.JLabel();
        boxLoai = new javax.swing.JComboBox<>();
        lblLoai = new javax.swing.JLabel();
        btnThem = new javax.swing.JButton();
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
        txtAnh = new javax.swing.JTextField();
        lblAnh = new javax.swing.JLabel();
        Anh = new javax.swing.JPanel();
        btnMo = new javax.swing.JButton();
        btnLammoi = new javax.swing.JButton();
        btnLoc = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(200, 173, 127));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("DANH SÁCH SẢN PHẨM");

        tblSanpham.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Mã quần áo", "Tên quần áo", "Thể loại", "Chất liệu", "Đối tượng",
                "Kích cỡ", "Màu", "Mùa", "Nơi sản xuất", "Đơn giá bán",
                "Đơn giá nhập", "Số lượng", "Ảnh"
            }
        ));
        jScrollPane1.setViewportView(tblSanpham);

        lblMaquanao.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMaquanao.setText("Mã quần áo:");

        lblTenquanao.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblTenquanao.setText("Tên quần áo:");

        btnXoa.setText("Xóa");

        btnCapnhat.setText("Cập nhật");

        lblDongianhap.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblDongianhap.setText("Đơn giá nhập:");

        boxLoai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        boxLoai.setPreferredSize(new java.awt.Dimension(100, 28));

        lblLoai.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblLoai.setText("Loại:");

        btnThem.setText("Thêm");

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

        lblAnh.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblAnh.setText("Ảnh:");

        javax.swing.GroupLayout AnhLayout = new javax.swing.GroupLayout(Anh);
        Anh.setLayout(AnhLayout);
        AnhLayout.setHorizontalGroup(
            AnhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 129, Short.MAX_VALUE)
        );
        AnhLayout.setVerticalGroup(
            AnhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 191, Short.MAX_VALUE)
        );

        btnMo.setText("Mở");

        btnLammoi.setText("Làm mới");

        btnLoc.setText("Lọc");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(38, 38, 38)
                                        .addComponent(lblMaquanao)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtMaquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(31, 31, 31)
                                        .addComponent(lblTenquanao)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtTenquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(30, 30, 30)
                                        .addComponent(lblDongianhap)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtDongianhap, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(56, 56, 56)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(lblChatlieu)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(boxChatlieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(lblLoai)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(boxLoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(lblDoituong)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(boxDoituong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(lblKichco)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(boxKichco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(lblMua)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(boxMua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(lblNoisanxuat)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(boxNoisanxuat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(lblMau)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(boxMau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(lblAnh)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtAnh, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnMo, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Anh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(186, 186, 186)
                            .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(65, 65, 65)
                            .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(68, 68, 68)
                            .addComponent(btnCapnhat, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(61, 61, 61)
                            .addComponent(btnLammoi, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(206, 206, 206)
                            .addComponent(lblDongiaban)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtDongiaban, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(79, 79, 79)
                            .addComponent(lblSoluong)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtSoluong, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 978, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(262, 262, 262)
                            .addComponent(jLabel1))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblMaquanao)
                            .addComponent(lblTenquanao)
                            .addComponent(txtTenquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMaquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(boxLoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblLoai))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(boxChatlieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblChatlieu))
                                .addGap(19, 19, 19)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(boxDoituong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblDoituong)))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(boxKichco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(lblKichco))
                                            .addGap(65, 65, 65))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGap(65, 65, 65)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(boxMua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(lblMua))))
                                    .addGap(5, 5, 5))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(70, 70, 70)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(boxNoisanxuat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblNoisanxuat)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGap(3, 3, 3)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(lblAnh)
                                                    .addComponent(txtAnh, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btnMo, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(boxMau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(lblMau)))
                                        .addGap(19, 19, 19)))))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblDongianhap)
                            .addComponent(txtDongianhap, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Anh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDongiaban)
                    .addComponent(txtDongiaban, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSoluong)
                    .addComponent(txtSoluong, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCapnhat, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLammoi, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(156, 156, 156))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 982, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 607, javax.swing.GroupLayout.PREFERRED_SIZE)
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
    private javax.swing.JButton btnCapnhat;
    private javax.swing.JButton btnLammoi;
    private javax.swing.JButton btnLoc;
    private javax.swing.JButton btnMo;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAnh;
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
    private javax.swing.JTable tblSanpham;
    private javax.swing.JTextField txtAnh;
    private javax.swing.JTextField txtDongiaban;
    private javax.swing.JTextField txtDongianhap;
    private javax.swing.JTextField txtMaquanao;
    private javax.swing.JTextField txtSoluong;
    private javax.swing.JTextField txtTenquanao;
    // End of variables declaration//GEN-END:variables
}
