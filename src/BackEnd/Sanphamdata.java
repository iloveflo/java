package BackEnd;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    public static boolean themSanPham(String tenQuanAo, String maQuanAo, String donGiaNhap, String donGiaBan, String soLuong, 
                                      String anh, JComboBox<String> boxLoai, JComboBox<String> boxChatlieu, 
                                      JComboBox<String> boxDoituong, JComboBox<String> boxKichco, JComboBox<String> boxMua, 
                                      JComboBox<String> boxMau, JComboBox<String> boxNoisanxuat) {
        
        // Validate fields
        if (tenQuanAo.isEmpty() || maQuanAo.isEmpty() || donGiaNhap.isEmpty() || donGiaBan.isEmpty() || 
            soLuong.isEmpty() || anh.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin!");
            return false;
        }

        // Check for duplicate MaQuanAo
        if (checkDuplicateMaQuanAo(maQuanAo)) {
            JOptionPane.showMessageDialog(null, "Mã quần áo đã tồn tại!");
            return false;
        }

        // Get the corresponding IDs for comboboxes
        int maLoai = getIdFromCombo(boxLoai,"MaLoai", "theloai", "TenLoai");
        int maChatLieu = getIdFromCombo(boxChatlieu,"MaChatLieu", "chatlieu", "TenChatLieu");
        int maDoituong = getIdFromCombo(boxDoituong,"MaDoiTuong", "doituong", "TenDoiTuong");
        int maKichCo = getIdFromCombo(boxKichco,"MaCo", "co", "TenCo");
        int maMua = getIdFromCombo(boxMua,"MaMua", "mua", "TenMua");
        int maMau = getIdFromCombo(boxMau,"MaMau", "mau", "TenMau");
        int maNSX = getIdFromCombo(boxNoisanxuat,"MaNSX", "noisanxuat", "TenNSX");

        // Add the new product to the database
        try (Connection conn = ketnoiCSDL.getConnection()) {
            String query = "INSERT INTO sanpham (MaQuanAo,TenQuanAo, MaLoai, MaCo, MaChatLieu, MaMau, MaDoiTuong, MaMua, MaNSX, SoLuong, Anh, DonGiaNhap, DonGiaBan) "
                         + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setString(1, maQuanAo);
                pst.setString(2, tenQuanAo);
                pst.setInt(3, maLoai);
                pst.setInt(4, maKichCo);
                pst.setInt(5, maChatLieu);
                pst.setInt(6, maMau);
                pst.setInt(7, maDoituong);
                pst.setInt(8, maMua);
                pst.setInt(9, maNSX);
                pst.setInt(10, Integer.parseInt(soLuong));
                pst.setString(11, anh);
                pst.setBigDecimal(12, new BigDecimal(donGiaNhap));
                pst.setBigDecimal(13, new BigDecimal(donGiaBan));

                int rowsAffected = pst.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Sản phẩm đã được thêm thành công!");
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Có lỗi xảy ra khi thêm sản phẩm.");
                    return false;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
            return false;
        }
    }

    private static boolean checkDuplicateMaQuanAo(String maQuanAo) {
        try (Connection conn = ketnoiCSDL.getConnection()) {
            String query = "SELECT COUNT(*) FROM sanpham WHERE MaQuanAo = ?";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setString(1, maQuanAo);
                ResultSet rs = pst.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi kiểm tra mã quần áo: " + e.getMessage());
        }
        return false;
    }

    public static int getIdFromCombo(JComboBox<String> comboBox, String tableName1,String tableName2 , String columnName) {
        String selectedValue = (String) comboBox.getSelectedItem();
        try (Connection conn = ketnoiCSDL.getConnection()) {
            String query = "SELECT "+ tableName1 + " FROM " + tableName2 + " WHERE " + columnName + " = ?";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setString(1, selectedValue);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi lấy ID từ bảng " + tableName2 + ": " + e.getMessage());
        }
        return -1; // Return -1 if not found
    }
    public static boolean xoaSanPham(String maQuanAo) {
        if (maQuanAo == null || maQuanAo.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập mã quần áo cần xóa!");
            return false;
        }

        String sql = "DELETE FROM sanpham WHERE MaQuanAo = ?";
        try (Connection conn = ketnoiCSDL.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maQuanAo);
            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(null, "Xóa sản phẩm thành công!");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Không tìm thấy sản phẩm với mã: " + maQuanAo);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi xóa sản phẩm: " + e.getMessage());
        }
        return false;
    }
    public static boolean capNhatSanPham(
        String maQuanAo, String tenQuanAo, int maLoai, int maCo, int maChatLieu,
        int maMau, int maDoiTuong, int maMua, int maNSX, int soLuong,
        String anh, double donGiaNhap, double donGiaBan
    ) {
        String sql = "UPDATE sanpham SET TenQuanAo=?, MaLoai=?, MaCo=?, MaChatLieu=?, MaMau=?, MaDoiTuong=?, MaMua=?, MaNSX=?, SoLuong=?, Anh=?, DonGiaNhap=?, DonGiaBan=? WHERE MaQuanAo=?";

        try (Connection conn = ketnoiCSDL.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, tenQuanAo);
            pst.setInt(2, maLoai);
            pst.setInt(3, maCo);
            pst.setInt(4, maChatLieu);
            pst.setInt(5, maMau);
            pst.setInt(6, maDoiTuong);
            pst.setInt(7, maMua);
            pst.setInt(8, maNSX);
            pst.setInt(9, soLuong);
            pst.setString(10, anh);
            pst.setDouble(11, donGiaNhap);
            pst.setDouble(12, donGiaBan);
            pst.setString(13, maQuanAo); // KHÔNG thay đổi mã

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Cập nhật sản phẩm thành công!");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Không tìm thấy sản phẩm để cập nhật!");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
        }

        return false;
    }
}