package BackEnd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;
import java.awt.Toolkit;
import java.io.File;

public class DonHangDAO {
     public static DefaultTableModel timKiemTrongGioHang(String maKH) {
        String[] columnNames = {"Mã Khách hàng", "Mã Quần Áo", "Đơn Giá Bán", "Số Lượng Đặt", "Tổng Tiền", "Ảnh"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try (Connection conn = ketnoiCSDL.getConnection()) {
            // Câu truy vấn SQL để tìm kiếm chính xác theo mã khách hàng
            String sql = "SELECT g.MaKhachHang, g.MaQuanAo, g.DonGiaBan, g.SoLuongDat, g.TongTien, s.Anh " +
                        "FROM GioHang g " +
                        "JOIN SanPham s ON g.MaQuanAo = s.MaQuanAo " +
                        "WHERE g.MaKhachHang = ?"; // Tìm kiếm chính xác mã khách hàng

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, maKH); // Truyền vào mã khách hàng tìm kiếm chính xác
            ResultSet rs = stmt.executeQuery();


            while (rs.next()) {
                // Lấy các giá trị từ ResultSet
                String maKHResult = rs.getString("MaKhachHang");
                String maQA = rs.getString("MaQuanAo");
                double donGia = rs.getDouble("DonGiaBan");
                int soLuong = rs.getInt("SoLuongDat");
                double tongTien = rs.getDouble("TongTien");
                String tenFileAnh = rs.getString("Anh");

                // Xử lý ảnh (Nếu có ảnh, load từ thư mục)
                ImageIcon icon = null;
                if (tenFileAnh != null && !tenFileAnh.isEmpty()) {
                    File imgFile = new File(tenFileAnh); // Kiểm tra đường dẫn ảnh
                    if (imgFile.exists()) {
                        Image img = new ImageIcon(imgFile.getAbsolutePath()).getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(img);
                    }
                }

                // Thêm dữ liệu vào model
                model.addRow(new Object[] {
                    maKHResult,  // Mã khách hàng
                    maQA,        // Mã quần áo
                    donGia,      // Đơn giá bán
                    soLuong,     // Số lượng đặt
                    tongTien,    // Tổng tiền
                    icon         // Ảnh
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }
}
