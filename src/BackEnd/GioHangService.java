package BackEnd;

import java.sql.*;
import javax.swing.*;

public class GioHangService {

    public static void themVaoGioHang(String maQuanAo, String soLuongStr, String donGiaBanStr) {
        if (SessionManager.getMaTaiKhoan() == null || SessionManager.getMaTaiKhoan().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Bạn chưa đăng nhập!");
            return;
        }

        if (maQuanAo == null || maQuanAo.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn sản phẩm!");
            return;
        }

        if (soLuongStr == null || soLuongStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập số lượng đặt!");
            return;
        }

        int soLuong;
        try {
            soLuong = Integer.parseInt(soLuongStr.trim());
            if (soLuong <= 0) {
                JOptionPane.showMessageDialog(null, "Số lượng đặt phải lớn hơn 0!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Số lượng đặt phải là số nguyên hợp lệ!");
            return;
        }

        try (Connection conn = ketnoiCSDL.getConnection()) {
            // Kiểm tra khách hàng tồn tại
            String sqlCheckKH = "SELECT MaKhachHang FROM khachhang WHERE MaTaiKhoan = ?";
            try (PreparedStatement pst = conn.prepareStatement(sqlCheckKH)) {
                pst.setString(1, SessionManager.getMaTaiKhoan());
                try (ResultSet rs = pst.executeQuery()) {
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(null, "Không tìm thấy khách hàng!");
                        return;
                    }
                    String maKhachHang = rs.getString("MaKhachHang");

                    // Kiểm tra sản phẩm tồn tại
                    String sqlCheckSP = "SELECT COUNT(*) FROM sanpham WHERE MaQuanAo = ?";
                    try (PreparedStatement pstCheckSP = conn.prepareStatement(sqlCheckSP)) {
                        pstCheckSP.setString(1, maQuanAo);
                        try (ResultSet rsCheckSP = pstCheckSP.executeQuery()) {
                            if (rsCheckSP.next() && rsCheckSP.getInt(1) == 0) {
                                JOptionPane.showMessageDialog(null, "Sản phẩm không tồn tại!");
                                return;
                            }
                        }
                    }

                    // Thêm vào giỏ hàng
                    String insertGio = "INSERT INTO giohang (MaKhachHang, MaQuanAo, DonGiaBan, SoLuongDat) " +
                            "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE SoLuongDat = SoLuongDat + ?";
                    try (PreparedStatement pstInsert = conn.prepareStatement(insertGio)) {
                        pstInsert.setString(1, maKhachHang);
                        pstInsert.setString(2, maQuanAo);
                        pstInsert.setBigDecimal(3, new java.math.BigDecimal(donGiaBanStr));
                        pstInsert.setInt(4, soLuong);
                        pstInsert.setInt(5, soLuong);

                        pstInsert.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Thêm vào giỏ hàng thành công!");
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Lỗi khi thêm vào giỏ hàng: " + ex.getMessage());
        }
    }

    public static void xoaSanPhamKhoiGio(String maQuanAo, String maKhachHang) {
        String checkSql = "SELECT TrangThai FROM GioHang WHERE MaKhachHang = ? AND MaQuanAo = ?";
        String deleteSql = "DELETE FROM GioHang WHERE MaKhachHang = ? AND MaQuanAo = ?";

        try (Connection conn = ketnoiCSDL.getConnection();
            PreparedStatement checkPst = conn.prepareStatement(checkSql)) {

            // Check trạng thái trước
            checkPst.setString(1, maKhachHang);
            checkPst.setString(2, maQuanAo);
            try (ResultSet rs = checkPst.executeQuery()) {
                if (rs.next()) {
                    int trangThai = rs.getInt("TrangThai");
                    if (trangThai != 0) {
                        JOptionPane.showMessageDialog(null, "Không thể xóa sản phẩm đã được đặt (trạng thái khác 0)!");
                        return; // Dừng không xóa
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Không tìm thấy sản phẩm trong giỏ hàng để xóa.");
                    return; // Dừng không xóa
                }
            }

            // Nếu trạng thái = 0, tiến hành xóa
            try (PreparedStatement deletePst = conn.prepareStatement(deleteSql)) {
                deletePst.setString(1, maKhachHang);
                deletePst.setString(2, maQuanAo);

                int rowsAffected = deletePst.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Xóa sản phẩm khỏi giỏ hàng thành công!");
                } else {
                    JOptionPane.showMessageDialog(null, "Xóa không thành công, vui lòng thử lại.");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi xóa sản phẩm: " + e.getMessage());
        }
    }
}
