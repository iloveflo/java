package BackEnd;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Gộp lớp KhachhangDTO và KhachhangDAO
public class KhachhangData {
    // DTO - Đối tượng khách hàng
    public static class Khachhang {
        private String maKhachHang;
        private String tenKhach;
        private String diaChi;
        private String soDienThoai;
        private String email;

        public Khachhang(String maKhachHang, String tenKhach, String diaChi, String soDienThoai, String email) {
            this.maKhachHang = maKhachHang;
            this.tenKhach = tenKhach;
            this.diaChi = diaChi;
            this.soDienThoai = soDienThoai;
            this.email = email;
        }

        public String getMaKhachHang() { return maKhachHang; }
        public String getTenKhach() { return tenKhach; }
        public String getDiaChi() { return diaChi; }
        public String getSoDienThoai() { return soDienThoai; }
        public String getEmail() { return email; }
    }

    // DAO - Lấy dữ liệu từ MySQL
    public static List<Khachhang> getAllKhachHang() {
        List<Khachhang> list = new ArrayList<>();
        String sql = "SELECT MaKhachHang, TenKhach, DiaChi, SoDienThoai, Email FROM khachhang";

        try (Connection conn = ketnoiCSDL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Khachhang kh = new Khachhang(
                        rs.getString("MaKhachHang"),
                        rs.getString("TenKhach"),
                        rs.getString("DiaChi"),
                        rs.getString("SoDienThoai"),
                        rs.getString("Email")
                );
                list.add(kh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Trong class KhachhangData
public static boolean deleteKhachHangAndTaiKhoan(String maKhachHang) {
    String getMaTaiKhoan = "SELECT MaTaiKhoan FROM khachhang WHERE MaKhachHang = ?";
    String deleteKhach = "DELETE FROM khachhang WHERE MaKhachHang = ?";
    String deleteTaiKhoan = "DELETE FROM taikhoan WHERE MaTaiKhoan = ?";
    try (Connection conn = ketnoiCSDL.getConnection()) {
        conn.setAutoCommit(false);
        PreparedStatement stmt1 = conn.prepareStatement(getMaTaiKhoan);
        stmt1.setString(1, maKhachHang);
        ResultSet rs = stmt1.executeQuery();
        if (rs.next()) {
            String maTK = rs.getString("MaTaiKhoan");

            PreparedStatement stmt2 = conn.prepareStatement(deleteKhach);
            stmt2.setString(1, maKhachHang);
            stmt2.executeUpdate();

            PreparedStatement stmt3 = conn.prepareStatement(deleteTaiKhoan);
            stmt3.setString(1, maTK);
            stmt3.executeUpdate();

            conn.commit();
            return true;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

public static Khachhang getKhachHangByID(String maKH) {
    String sql = "SELECT * FROM khachhang WHERE MaKhachHang = ?";
    try (Connection conn = ketnoiCSDL.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, maKH);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new Khachhang(
                rs.getString("MaKhachHang"),
                rs.getString("TenKhach"),
                rs.getString("DiaChi"),
                rs.getString("SoDienThoai"),
                rs.getString("Email")
            );
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

public static boolean updateKhachHang(Khachhang kh) {
    String sql = "UPDATE khachhang SET TenKhach=?, DiaChi=?, SoDienThoai=?, Email=? WHERE MaKhachHang=?";
    try (Connection conn = ketnoiCSDL.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, kh.getTenKhach());
        stmt.setString(2, kh.getDiaChi());
        stmt.setString(3, kh.getSoDienThoai());
        stmt.setString(4, kh.getEmail());
        stmt.setString(5, kh.getMaKhachHang());
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

}
