/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import BackEnd.*;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Neo 16
 */
public class Thongke extends javax.swing.JPanel {

    /**
     * Creates new form Thongke1
     */
    public Thongke() {
        initComponents();
        loadDoanhThuTheoThang();
        tblThongke.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tblThongke.getSelectedRow();
                if (selectedRow >= 0) {
                    String thangNam = tblThongke.getValueAt(selectedRow, 0).toString(); // dạng: "3/2024"
                    String[] parts = thangNam.split("/");

                    if (parts.length == 2) {
                        String thang = parts[0].trim();
                        cbThang.setSelectedItem(thang);
                    }

                    txtSoluong.setText(tblThongke.getValueAt(selectedRow, 1).toString());
                    txtDoanhthu.setText(tblThongke.getValueAt(selectedRow, 2).toString());
                    txtLoinhuan.setText(tblThongke.getValueAt(selectedRow, 3).toString());
                }
            }
        });

        cbThang.addActionListener(e -> {
            String thangChon = cbThang.getSelectedItem().toString(); // ví dụ "5"
            locBangTheoThang(thangChon);
        });
    }

    public void locBangTheoThang(String thangChon) {
        DefaultTableModel model = (DefaultTableModel) tblThongke.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        tblThongke.setRowSorter(sorter);

        // Lọc theo cột 0 (cột "Tháng") với dữ liệu kiểu "5/2024"
        RowFilter<DefaultTableModel, Object> filter = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String value = entry.getStringValue(0); // lấy giá trị cột "Tháng"
                String[] parts = value.split("/");
                if (parts.length == 2) {
                    return parts[0].trim().equals(thangChon); // so sánh tháng
                }
                return false;
            }
        };

        sorter.setRowFilter(filter);
    }


    public void loadDoanhThuTheoThang() {
    DefaultTableModel model = (DefaultTableModel) tblThongke.getModel();
    model.setRowCount(0); // Xóa dữ liệu cũ

    String queryGetMonths = """
        SELECT DISTINCT YEAR(NgayBan) AS Nam, MONTH(NgayBan) AS Thang, 
               CONCAT(YEAR(NgayBan), '-', LPAD(MONTH(NgayBan), 2, '0')) AS ThangNam
        FROM hoadonban
        ORDER BY Nam, Thang
        """;

    try (Connection conn = ketnoiCSDL.getConnection();
         PreparedStatement stmtMonths = conn.prepareStatement(queryGetMonths);
         ResultSet rsMonths = stmtMonths.executeQuery()) {

        while (rsMonths.next()) {
            int nam = rsMonths.getInt("Nam");
            int thang = rsMonths.getInt("Thang");
            String thangNamHienThi = thang + "/" + nam;

            // 1. Tổng số lượng
            String querySoLuong = """
                SELECT SUM(ctb.SoLuong) AS TongSoLuong
                FROM chitiethoadonban ctb
                JOIN hoadonban hdb ON ctb.SoHoaDonBan = hdb.SoHoaDonBan
                WHERE YEAR(hdb.NgayBan) = ? AND MONTH(hdb.NgayBan) = ?
                """;
            int tongSoLuong = 0;
            try (PreparedStatement stmtSL = conn.prepareStatement(querySoLuong)) {
                stmtSL.setInt(1, nam);
                stmtSL.setInt(2, thang);
                try (ResultSet rsSL = stmtSL.executeQuery()) {
                    if (rsSL.next()) {
                        tongSoLuong = rsSL.getInt("TongSoLuong");
                    }
                }
            }

            // 2. Tổng doanh thu
            String queryDoanhThu = """
                SELECT SUM(TongTien) AS TongDoanhThu
                FROM hoadonban
                WHERE YEAR(NgayBan) = ? AND MONTH(NgayBan) = ?
                """;
            double tongDoanhThu = 0;
            try (PreparedStatement stmtDT = conn.prepareStatement(queryDoanhThu)) {
                stmtDT.setInt(1, nam);
                stmtDT.setInt(2, thang);
                try (ResultSet rsDT = stmtDT.executeQuery()) {
                    if (rsDT.next()) {
                        tongDoanhThu = rsDT.getDouble("TongDoanhThu");
                    }
                }
            }

            // 3. Tổng lợi nhuận
            String queryLoiNhuan = """
                SELECT 
                    (SELECT SUM(TongTien) FROM hoadonban 
                     WHERE YEAR(NgayBan) = ? AND MONTH(NgayBan) = ?) -
                    (SELECT SUM(
                        (SELECT AVG(ctn.DonGia * (1 - ctn.GiamGia / 100))
                         FROM chitiethoadonnhap ctn
                         WHERE ctn.MaQuanAo = ctb.MaQuanAo) * ctb.SoLuong
                    )
                    FROM chitiethoadonban ctb
                    JOIN hoadonban hdb ON ctb.SoHoaDonBan = hdb.SoHoaDonBan
                    WHERE YEAR(hdb.NgayBan) = ? AND MONTH(hdb.NgayBan) = ?) 
                AS TongLoiNhuan
                """;
            double tongLoiNhuan = 0;
            try (PreparedStatement stmtLN = conn.prepareStatement(queryLoiNhuan)) {
                stmtLN.setInt(1, nam);
                stmtLN.setInt(2, thang);
                stmtLN.setInt(3, nam);
                stmtLN.setInt(4, thang);
                try (ResultSet rsLN = stmtLN.executeQuery()) {
                    if (rsLN.next()) {
                        tongLoiNhuan = rsLN.getDouble("TongLoiNhuan");
                    }
                }
            }

            // Thêm vào bảng
            model.addRow(new Object[]{
                    thangNamHienThi,
                    tongSoLuong,
                    String.format("%,.0f", tongDoanhThu),
                    String.format("%,.0f", tongLoiNhuan)
            });
        }

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, "Lỗi khi tải dữ liệu doanh thu: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
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
        tblThongke = new javax.swing.JTable();
        lblThang = new javax.swing.JLabel();
        lblDoanhthu = new javax.swing.JLabel();
        txtDoanhthu = new javax.swing.JTextField();
        txtSoluong = new javax.swing.JTextField();
        lblSoluong = new javax.swing.JLabel();
        txtLoinhuan = new javax.swing.JTextField();
        lblLoinhuan = new javax.swing.JLabel();
        cbThang = new javax.swing.JComboBox<>();

        jPanel1.setBackground(new java.awt.Color(173, 216, 230));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("THỐNG KÊ DOANH THU");

        tblThongke.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Tháng", "Số lượng đã bán", "Tổng Doanh thu", "Tổng lợi nhuận"
            }
        ));
        jScrollPane1.setViewportView(tblThongke);

        lblThang.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblThang.setText("Tháng:");

        lblDoanhthu.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblDoanhthu.setText("Doanh Thu:");

        lblSoluong.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblSoluong.setText("Số lượng đã bán:");

        lblLoinhuan.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblLoinhuan.setText("Lợi nhuận:");

        cbThang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4","5", "6", "7", "8","9", "10", "11", "12"}));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 975, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(82, 82, 82)
                                .addComponent(lblThang)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbThang, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblDoanhthu)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDoanhthu, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblSoluong)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSoluong, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addComponent(lblLoinhuan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtLoinhuan, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(326, 326, 326)
                        .addComponent(jLabel1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblThang)
                    .addComponent(lblDoanhthu)
                    .addComponent(txtDoanhthu, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbThang, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSoluong)
                    .addComponent(txtSoluong, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLoinhuan)
                    .addComponent(txtLoinhuan, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(82, 82, 82)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(97, 97, 97))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cbThang;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDoanhthu;
    private javax.swing.JLabel lblLoinhuan;
    private javax.swing.JLabel lblSoluong;
    private javax.swing.JLabel lblThang;
    private javax.swing.JTable tblThongke;
    private javax.swing.JTextField txtDoanhthu;
    private javax.swing.JTextField txtLoinhuan;
    private javax.swing.JTextField txtSoluong;
    // End of variables declaration//GEN-END:variables
}
