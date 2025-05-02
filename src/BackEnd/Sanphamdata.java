package BackEnd;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

public class Sanphamdata {
    public static void doDuLieuVaoComboBox(JComboBox<String> comboBox, String tenBang, String tenCot) {
        comboBox.removeAllItems(); // Xóa dữ liệu cũ nếu có

        String sql = "SELECT " + tenCot + " FROM " + tenBang;
        try (Connection conn = ketnoiCSDL.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String ten = rs.getString(tenCot);
                comboBox.addItem(ten);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải dữ liệu từ bảng " + tenBang + ": " + e.getMessage());
        }
    }
     public boolean kiemTraTrungMaQuanAo(int maQuanAo) {
        String sql = "SELECT MaQuanAo FROM sanpham WHERE MaQuanAo = ?";
        try (Connection conn = ketnoiCSDL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maQuanAo);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // true nếu trùng
        } catch (Exception e) {
            e.printStackTrace();
            return true; // lỗi thì mặc định không cho thêm
        }
    }

    public boolean themSanPham(int maQuanAo, String tenQuanAo, int maLoai, int maCo, int maChatLieu,
                               int maMau, int maDoiTuong, int maMua, int maNSX,
                               int soLuong, String anh, BigDecimal donGiaNhap, BigDecimal donGiaBan) {

        String sql = "INSERT INTO sanpham (MaQuanAo, TenQuanAo, MaLoai, MaCo, MaChatLieu, MaMau, MaDoiTuong, MaMua, MaNSX, SoLuong, Anh, DonGiaNhap, DonGiaBan) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ketnoiCSDL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maQuanAo);
            stmt.setString(2, tenQuanAo);
            stmt.setInt(3, maLoai);
            stmt.setInt(4, maCo);
            stmt.setInt(5, maChatLieu);
            stmt.setInt(6, maMau);
            stmt.setInt(7, maDoiTuong);
            stmt.setInt(8, maMua);
            stmt.setInt(9, maNSX);
            stmt.setInt(10, soLuong);
            stmt.setString(11, anh);
            stmt.setBigDecimal(12, donGiaNhap);
            stmt.setBigDecimal(13, donGiaBan);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
