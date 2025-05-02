package BackEnd;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import BackEnd.ketnoiCSDL;

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
    public static void themNhanVien(
        String maNV, String hoTen, String gioiTinh, String ngaySinh,
        String sdt, String diaChi, String email, String maCongViec
    ) {
        if (maNV.isEmpty() || hoTen.isEmpty() || gioiTinh.isEmpty() || ngaySinh.isEmpty()
            || sdt.isEmpty() || diaChi.isEmpty() || email.isEmpty() || maCongViec.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin.");
            return;
        }

        try (Connection conn = ketnoiCSDL.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu transaction
            // Kiểm tra trùng mã nhân viên
            if (isExist(conn, "SELECT MaNhanVien FROM nhanvien WHERE MaNhanVien = ?", maNV)) {
                JOptionPane.showMessageDialog(null, "Mã nhân viên đã tồn tại.");
                return;
            }

            // Kiểm tra trùng mã tài khoản
            if (isExist(conn, "SELECT MaTaiKhoan FROM taikhoan WHERE MaTaiKhoan = ?", maNV)) {
                JOptionPane.showMessageDialog(null, "Mã tài khoản đã tồn tại.");
                return;
            }

            // Kiểm tra trùng số điện thoại hoặc email ở cả 2 bảng
            if (isExist(conn, "SELECT * FROM nhanvien WHERE SoDienThoai = ? OR Email = ?", sdt, email) ||
                isExist(conn, "SELECT * FROM khachhang WHERE SoDienThoai = ? OR Email = ?", sdt, email)) {
                JOptionPane.showMessageDialog(null, "Số điện thoại hoặc email đã tồn tại.");
                return;
            }

            String insertTaiKhoan = "INSERT INTO taikhoan (MaTaiKhoan, TenDangNhap, MatKhau, LoaiTaiKhoan, DangNhap) "
                                  + "VALUES (?, ?, ?, 'NhanVien', 0)";
            try (PreparedStatement pstTK = conn.prepareStatement(insertTaiKhoan)) {
                pstTK.setString(1, maNV);
                pstTK.setString(2, maNV);
                pstTK.setString(3, maNV); // Có thể mã hóa mật khẩu tại đây nếu muốn
                pstTK.executeUpdate();
            }

            // Tìm mã công việc từ tên công việc
            String sqlFindMaCV = "SELECT MaCongViec FROM congviec WHERE TenCongViec = ?";
            int maCongViecID = -1;
            try (PreparedStatement pstFind = conn.prepareStatement(sqlFindMaCV)) {
                pstFind.setString(1, maCongViec);
                ResultSet rs = pstFind.executeQuery();
                if (rs.next()) {
                    maCongViecID = rs.getInt("MaCongViec");
                } else {
                    JOptionPane.showMessageDialog(null, "Không tìm thấy công việc: " + maCongViec);
                    return;
                }
            }
            // Insert vào bảng nhanvien
            String sql = "INSERT INTO nhanvien (MaNhanVien, TenNhanVien, GioiTinh, NgaySinh, SoDienThoai, DiaChi, Email, MaCongViec, MaTaiKhoan) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, Integer.parseInt(maNV));
                pst.setString(2, hoTen);
                pst.setString(3, gioiTinh);
                pst.setDate(4, java.sql.Date.valueOf(ngaySinh));
                pst.setString(5, sdt);
                pst.setString(6, diaChi);
                pst.setString(7, email);
                pst.setInt(8, maCongViecID); // mã công việc lấy từ bảng
                pst.setString(9, maNV);

                pst.executeUpdate();
                conn.commit(); // Mọi thứ thành công, xác nhận transaction
                JOptionPane.showMessageDialog(null, "Thêm nhân viên thành công!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi thêm nhân viên: " + e.getMessage());
        }
    }

    // Hàm kiểm tra tồn tại (1 tham số)
    private static boolean isExist(Connection conn, String query, String value) throws Exception {
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, value);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        }
    }

    // Hàm kiểm tra tồn tại (2 tham số)
    private static boolean isExist(Connection conn, String query, String v1, String v2) throws Exception {
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, v1);
            pst.setString(2, v2);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        }
    }

    public static boolean xoaNhanVien(String maNV) {
        try (Connection conn = ketnoiCSDL.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu transaction
    
            int affected1 = 0, affected2 = 0;
    
            // Xóa nhân viên
            String sqlNV = "DELETE FROM nhanvien WHERE MaNhanVien = ?";
            try (PreparedStatement pstNV = conn.prepareStatement(sqlNV)) {
                pstNV.setString(1, maNV);
                affected1 = pstNV.executeUpdate();
            }
    
            // Xóa tài khoản
            String sqlTK = "DELETE FROM taikhoan WHERE MaTaiKhoan = ?";
            try (PreparedStatement pstTK = conn.prepareStatement(sqlTK)) {
                pstTK.setString(1, maNV);
                affected2 = pstTK.executeUpdate();
            }
    
            conn.commit(); // Commit nếu không lỗi
    
            // Chỉ cần 1 trong 2 xóa được, vẫn coi là thành công
            return (affected1 > 0 || affected2 > 0);
    
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xóa nhân viên:\n" + e.toString());
            try {
                ketnoiCSDL.getConnection().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }
    
    public static List<Object[]> timKiemNhanVienTheoMa(String maNV) {
        List<Object[]> ketQua = new ArrayList<>();

        try (Connection conn = ketnoiCSDL.getConnection()) {
            String sql = "SELECT * FROM nhanvien WHERE MaNhanVien = ?";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, maNV);
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    Object[] row = {
                        rs.getString("MaNhanVien"),
                        rs.getString("TenNhanVien"),
                        rs.getString("GioiTinh"),
                        rs.getDate("NgaySinh"),
                        rs.getString("SoDienThoai"),
                        rs.getString("DiaChi"),
                        rs.getString("Email"),
                        rs.getString("MaCongViec"),
                        rs.getString("MaTaiKhoan")
                    };
                    ketQua.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tìm nhân viên: " + e.getMessage());
        }

        return ketQua;
    }


    public static boolean capNhatNhanVien(
            String maNV, String hoTen, String gioiTinh, String ngaySinh,
            String sdt, String diaChi, String email, String maCongViec
    ) {
        if (maNV.isEmpty() || hoTen.isEmpty() || gioiTinh.isEmpty() || ngaySinh.isEmpty()
                || sdt.isEmpty() || diaChi.isEmpty() || email.isEmpty() || maCongViec.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin.");
            return false;
        }

        try (Connection conn = ketnoiCSDL.getConnection()) {

            // Kiểm tra trùng SDT hoặc Email (ngoại trừ chính mã nhân viên này)
            String sqlCheck = "SELECT * FROM nhanvien WHERE (SoDienThoai = ? OR Email = ?) AND MaNhanVien <> ?";
            try (PreparedStatement pstCheck = conn.prepareStatement(sqlCheck)) {
                pstCheck.setString(1, sdt);
                pstCheck.setString(2, email);
                pstCheck.setString(3, maNV);
                ResultSet rs = pstCheck.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(null, "Số điện thoại hoặc email đã tồn tại ở nhân viên khác.");
                    return false;
                }
            }

            // Kiểm tra trong bảng khachhang
            String sqlCheckKH = "SELECT * FROM khachhang WHERE SoDienThoai = ? OR Email = ?";
            try (PreparedStatement pstKH = conn.prepareStatement(sqlCheckKH)) {
                pstKH.setString(1, sdt);
                pstKH.setString(2, email);
                ResultSet rsKH = pstKH.executeQuery();
                if (rsKH.next()) {
                    JOptionPane.showMessageDialog(null, "Số điện thoại hoặc email đã tồn tại ở khách hàng.");
                    return false;
                }
            }

            // Lấy MaCongViec từ TenCongViec
            String sqlFindMaCV = "SELECT MaCongViec FROM congviec WHERE TenCongViec = ?";
            int maCongViecID = -1;
            try (PreparedStatement pstFind = conn.prepareStatement(sqlFindMaCV)) {
                pstFind.setString(1, maCongViec);
                ResultSet rs = pstFind.executeQuery();
                if (rs.next()) {
                    maCongViecID = rs.getInt("MaCongViec");
                } else {
                    JOptionPane.showMessageDialog(null, "Không tìm thấy công việc: " + maCongViec);
                    return false;
                }
            }

            // Tiến hành cập nhật
            String sqlUpdate = "UPDATE nhanvien SET TenNhanVien = ?, GioiTinh = ?, NgaySinh = ?, "
                    + "SoDienThoai = ?, DiaChi = ?, Email = ?, MaCongViec = ? "
                    + "WHERE MaNhanVien = ?";

            try (PreparedStatement pst = conn.prepareStatement(sqlUpdate)) {
                pst.setString(1, hoTen);
                pst.setString(2, gioiTinh);
                pst.setDate(3, java.sql.Date.valueOf(ngaySinh));
                pst.setString(4, sdt);
                pst.setString(5, diaChi);
                pst.setString(6, email);
                pst.setInt(7, maCongViecID);  // Đúng kiểu int cho MaCongViec
                pst.setString(8, maNV);

                int affected = pst.executeUpdate();
                return affected > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật nhân viên:\n" + e.getMessage());
            return false;
        }
    }
}

