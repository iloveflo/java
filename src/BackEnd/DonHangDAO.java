package BackEnd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.sql.Statement;

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

    public static void lapHoaDon(String maQuanAo, int soLuongDat, double donGiaBan, String maKhachHang, String maNhanVien, JTable table) {
        try (Connection conn = ketnoiCSDL.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu transaction

            // Lấy số lượng sản phẩm hiện tại
            String sqlCheck = "SELECT SoLuong FROM SanPham WHERE MaQuanAo = ?";
            PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
            psCheck.setString(1, maQuanAo);
            ResultSet rs = psCheck.executeQuery();
            if (!rs.next()) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy sản phẩm!");
                return;
            }
            int soLuongHienTai = rs.getInt("SoLuong");
            if (soLuongHienTai < soLuongDat) {
                JOptionPane.showMessageDialog(null, "Không đủ số lượng sản phẩm trong kho!");
                return;
            }

            // Trừ số lượng sản phẩm
            String sqlUpdate = "UPDATE SanPham SET SoLuong = SoLuong - ? WHERE MaQuanAo = ?";
            PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
            psUpdate.setInt(1, soLuongDat);
            psUpdate.setString(2, maQuanAo);
            psUpdate.executeUpdate();

            // Tính tổng tiền
            double tongTien = soLuongDat * donGiaBan;

            // Tạo hóa đơn bán
            String sqlInsertHoaDon = "INSERT INTO HoaDonBan (MaKhachHang, NgayBan, MaNhanVien, TongTien) " +
                                     "VALUES (?, NOW(), ?, ?)";
            PreparedStatement psInsertHD = conn.prepareStatement(sqlInsertHoaDon, Statement.RETURN_GENERATED_KEYS);
            psInsertHD.setString(1, maKhachHang);
            psInsertHD.setString(2, maNhanVien);
            psInsertHD.setDouble(3, tongTien);
            psInsertHD.executeUpdate();
            ResultSet generatedKeys = psInsertHD.getGeneratedKeys();
            int soHoaDon = 0;
            if (generatedKeys.next()) {
                soHoaDon = generatedKeys.getInt(1);
            }

            // Tạo giảm giá ngẫu nhiên
            int giamGia = new Random().nextInt(50) + 1;
            double thanhTien = tongTien * (1 - giamGia / 100.0);

            // Thêm chi tiết hóa đơn
            String sqlInsertChiTiet = "INSERT INTO ChiTietHoaDonBan (SoHoaDonBan, MaQuanAo, SoLuong, GiamGia, ThanhTien) " +
                                      "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psChiTiet = conn.prepareStatement(sqlInsertChiTiet);
            psChiTiet.setInt(1, soHoaDon);
            psChiTiet.setString(2, maQuanAo);
            psChiTiet.setInt(3, soLuongDat);
            psChiTiet.setInt(4, giamGia);
            psChiTiet.setDouble(5, thanhTien);
            psChiTiet.executeUpdate();

            // Xóa khỏi giỏ hàng
            String sqlDelete = "DELETE FROM GioHang WHERE MaQuanAo = ? AND MaKhachHang = ?";
            PreparedStatement psDelete = conn.prepareStatement(sqlDelete);
            psDelete.setString(1, maQuanAo);
            psDelete.setString(2, maKhachHang);
            psDelete.executeUpdate();

            conn.commit(); // Hoàn tất
            JOptionPane.showMessageDialog(null, "Lập hóa đơn thành công!");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi lập hóa đơn: " + e.getMessage());
        }
    }
}
