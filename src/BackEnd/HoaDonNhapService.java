package BackEnd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import java.sql.Statement; 

public class HoaDonNhapService {
    public static boolean lapHoaDonNhap(int maQuanAo, int maNhanVien, int soLuong, double donGia, double giamGia, String tenNCC) {
        try (Connection conn = ketnoiCSDL.getConnection()) {

            // 1. Kiểm tra sản phẩm
            String sqlCheckSP = "SELECT COUNT(*) FROM sanpham WHERE MaQuanAo = ?";
            PreparedStatement psCheck = conn.prepareStatement(sqlCheckSP);
            psCheck.setInt(1, maQuanAo);
            ResultSet rsCheck = psCheck.executeQuery();
            rsCheck.next();
            if (rsCheck.getInt(1) == 0) {
                JOptionPane.showMessageDialog(null, "Sản phẩm không tồn tại trong bảng sanpham!");
                return false;
            }

            // 2. Lấy mã nhà cung cấp
            String sqlGetMaNCC = "SELECT MaNCC FROM nhacungcap WHERE TenNCC = ?";
            PreparedStatement psNCC = conn.prepareStatement(sqlGetMaNCC);
            psNCC.setString(1, tenNCC);
            ResultSet rsNCC = psNCC.executeQuery();
            if (!rsNCC.next()) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy nhà cung cấp!");
                return false;
            }
            int maNCC = rsNCC.getInt("MaNCC");

            // 3. Cập nhật bảng sản phẩm
            String sqlUpdateSP = "UPDATE sanpham SET SoLuong = SoLuong + ?, DonGiaNhap = ? WHERE MaQuanAo = ?";
            PreparedStatement psUpdateSP = conn.prepareStatement(sqlUpdateSP);
            psUpdateSP.setInt(1, soLuong);
            psUpdateSP.setDouble(2, donGia);
            psUpdateSP.setInt(3, maQuanAo);
            psUpdateSP.executeUpdate();

            // 4. Thêm vào bảng HoaDonNhap
            String sqlInsertHD = "INSERT INTO hoadonnhap (MaNhanVien, NgayNhap, MaNCC, TongTien) VALUES (?, NOW(), ?, ?)";
            PreparedStatement psInsertHD = conn.prepareStatement(sqlInsertHD, Statement.RETURN_GENERATED_KEYS);
            psInsertHD.setInt(1, maNhanVien);
            psInsertHD.setInt(2, maNCC);
            psInsertHD.setDouble(3, soLuong * donGia);
            psInsertHD.executeUpdate();

            ResultSet rsHD = psInsertHD.getGeneratedKeys();
            int soHoaDonMoi = -1;
            if (rsHD.next()) {
                soHoaDonMoi = rsHD.getInt(1);
            }

            // 5. Thêm chi tiết hóa đơn
            String sqlInsertCT = "INSERT INTO chitiethoadonnhap (SoHoaDonNhap, MaQuanAo, SoLuong, DonGia, GiamGia, ThanhTien) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement psCT = conn.prepareStatement(sqlInsertCT);
            psCT.setInt(1, soHoaDonMoi);
            psCT.setInt(2, maQuanAo);
            psCT.setInt(3, soLuong);
            psCT.setDouble(4, donGia);
            psCT.setDouble(5, giamGia);
            psCT.setDouble(6, soLuong * donGia * (1 - giamGia / 100.0));
            psCT.executeUpdate();

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi: " + ex.getMessage());
            return false;
        }
    }
    public static DefaultTableModel timKiemHoaDonNhap(String soHoaDonNhap, String tenNCC) {
        String query = "SELECT hd.SoHoaDonNhap, hd.MaNhanVien, ncc.TenNCC, ct.MaQuanAo, ct.SoLuong, ct.DonGia, ct.GiamGia, " +
                       "ct.SoLuong * ct.DonGia * (1 - ct.GiamGia / 100) AS ThanhTien " +
                       "FROM HoaDonNhap hd " +
                       "JOIN NhaCungCap ncc ON hd.MaNCC = ncc.MaNCC " +
                       "JOIN ChiTietHoaDonNhap ct ON hd.SoHoaDonNhap = ct.SoHoaDonNhap " +
                       "WHERE hd.SoHoaDonNhap LIKE ? OR ncc.TenNCC LIKE ?";

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[] {
            "Số Hóa Đơn Nhập", "Mã Nhân Viên", "Tên Nhà Cung Cấp",
            "Mã Quần Áo", "Số Lượng", "Đơn Giá", "Giảm Giá", "Thành Tiền"
        });

        try (Connection conn = ketnoiCSDL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + soHoaDonNhap + "%");
            stmt.setString(2, "%" + tenNCC + "%");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getString("SoHoaDonNhap"),
                    rs.getString("MaNhanVien"),
                    rs.getString("TenNCC"),
                    rs.getString("MaQuanAo"),
                    rs.getInt("SoLuong"),
                    rs.getDouble("DonGia"),
                    rs.getDouble("GiamGia"),
                    rs.getDouble("ThanhTien")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return model;
    }
}
