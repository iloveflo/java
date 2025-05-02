package BackEnd;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanvienData {

    // DTO
    public static class Nhanvien {
        private int maNhanVien;
        private String tenNhanVien;
        private String gioiTinh;
        private Date ngaySinh;
        private String soDienThoai;
        private String diaChi;
        private String email;
        private int maCongViec;
        private String tenCongViec;

        public Nhanvien(int maNhanVien, String tenNhanVien, String gioiTinh, Date ngaySinh,
                        String soDienThoai, String diaChi, String email, int maCongViec, String tenCongViec) {
            this.maNhanVien = maNhanVien;
            this.tenNhanVien = tenNhanVien;
            this.gioiTinh = gioiTinh;
            this.ngaySinh = ngaySinh;
            this.soDienThoai = soDienThoai;
            this.diaChi = diaChi;
            this.email = email;
            this.maCongViec = maCongViec;
            this.tenCongViec = tenCongViec;
        }

        public int getMaNhanVien() { return maNhanVien; }
        public String getTenNhanVien() { return tenNhanVien; }
        public String getGioiTinh() { return gioiTinh; }
        public Date getNgaySinh() { return ngaySinh; }
        public String getSoDienThoai() { return soDienThoai; }
        public String getDiaChi() { return diaChi; }
        public String getEmail() { return email; }
        public int getMaCongViec() { return maCongViec; }
        public String getTenCongViec() { return tenCongViec; }
    }

    // DAO: lấy danh sách nhân viên JOIN công việc
    public static List<Nhanvien> getAllNhanVien() {
        List<Nhanvien> list = new ArrayList<>();
        String sql = "SELECT nv.*, cv.TenCongViec FROM nhanvien nv " +
                     "LEFT JOIN congviec cv ON nv.MaCongViec = cv.MaCongViec";

        try (Connection conn = ketnoiCSDL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Nhanvien nv = new Nhanvien(
                        rs.getInt("MaNhanVien"),
                        rs.getString("TenNhanVien"),
                        rs.getString("GioiTinh"),
                        rs.getDate("NgaySinh"),
                        rs.getString("SoDienThoai"),
                        rs.getString("DiaChi"),
                        rs.getString("Email"),
                        rs.getInt("MaCongViec"),
                        rs.getString("TenCongViec")
                );
                list.add(nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

