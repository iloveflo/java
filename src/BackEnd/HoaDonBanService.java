package BackEnd;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class HoaDonBanService {
    public static void xuatHoaDon(String soHoaDon) {
        if (soHoaDon.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn hóa đơn để in.");
            return;
        }
        try (Connection conn = ketnoiCSDL.getConnection()) {
            String query = """
                SELECT 
                    hdb.SoHoaDonBan, hdb.NgayBan, hdb.TongTien,
                    kh.TenKhach, kh.DiaChi AS DiaChiKhach, kh.SoDienThoai AS SDTKhach, kh.Email AS EmailKhach,
                    nv.TenNhanVien, nv.SoDienThoai AS SDTNhanVien,
                    sp.TenQuanAo, cthd.SoLuong, cthd.GiamGia, cthd.ThanhTien
                FROM hoadonban hdb
                JOIN khachhang kh ON hdb.MaKhachHang = kh.MaKhachHang
                JOIN nhanvien nv ON hdb.MaNhanVien = nv.MaNhanVien
                JOIN chitiethoadonban cthd ON hdb.SoHoaDonBan = cthd.SoHoaDonBan
                JOIN sanpham sp ON cthd.MaQuanAo = sp.MaQuanAo
                WHERE hdb.SoHoaDonBan = ?;
            """;

            PreparedStatement cmd = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            cmd.setString(1, soHoaDon);
            ResultSet rs = cmd.executeQuery();
            
            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("HoaDon");

            int row = 0;
            
            Row titleRow = sheet.createRow(row++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("HÓA ĐƠN BÁN HÀNG - BDTHD");
            CellStyle styleTitle = wb.createCellStyle();
            Font fontTitle = wb.createFont();
            fontTitle.setBold(true);
            fontTitle.setFontHeightInPoints((short) 18);
            styleTitle.setFont(fontTitle);
            styleTitle.setAlignment(HorizontalAlignment.CENTER);
            titleCell.setCellStyle(styleTitle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

            if (rs.next()) {
                Row infoRow = sheet.createRow(row++);
                infoRow.createCell(0).setCellValue("Số hóa đơn: " + soHoaDon);

                sheet.createRow(row++).createCell(0).setCellValue("Ngày bán: " + rs.getDate("NgayBan"));
                sheet.createRow(row++).createCell(0).setCellValue("Khách hàng: " + rs.getString("TenKhach"));
                sheet.createRow(row++).createCell(0).setCellValue("Số điện thoại: " + rs.getString("SDTKhach"));
                sheet.createRow(row++).createCell(0).setCellValue("Email: " + rs.getString("EmailKhach"));
                sheet.createRow(row++).createCell(0).setCellValue("Nhân viên bán: " + rs.getString("TenNhanVien"));
                sheet.createRow(row++).createCell(0).setCellValue("Liên hệ hỗ trợ: " + rs.getString("SDTNhanVien"));
                row++;

                Row header = sheet.createRow(row++);
                header.createCell(0).setCellValue("Tên sản phẩm");
                header.createCell(1).setCellValue("Số lượng");
                header.createCell(2).setCellValue("Giảm giá (%)");
                header.createCell(3).setCellValue("Thành tiền");

                rs.beforeFirst();
                while (rs.next()) {
                    Row r = sheet.createRow(row++);
                    r.createCell(0).setCellValue(rs.getString("TenQuanAo"));
                    r.createCell(1).setCellValue(rs.getInt("SoLuong"));
                    r.createCell(2).setCellValue(rs.getFloat("GiamGia"));
                    r.createCell(3).setCellValue(rs.getDouble("ThanhTien"));
                }

                Row totalRow = sheet.createRow(row++);
                totalRow.createCell(2).setCellValue("Tổng tiền:");
                totalRow.createCell(3).setCellFormula("SUM(D9:D" + (row - 1) + ")");

                for (int i = 0; i <= 3; i++) {
                    sheet.autoSizeColumn(i);
                }

                String fileName = "HoaDon_" + soHoaDon.replaceAll("[^a-zA-Z0-9]", "_") + ".xlsx";
                String filePath = "D:\\ProjectJava\\ClothingStore\\" + fileName;
                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    wb.write(fos);
                }
                wb.close();

                JOptionPane.showMessageDialog(null, "✅ Xuất hóa đơn thành công!\nFile: " + filePath);
                Runtime.getRuntime().exec("explorer " + filePath);
            } else {
                JOptionPane.showMessageDialog(null, "Không tìm thấy hóa đơn!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xuất hóa đơn: " + ex.getMessage());
        }
    }

    public static void timKiemHoaDonTheoMaKhachHang(DefaultTableModel model, String maKH) {
        model.setRowCount(0); // Xóa dữ liệu cũ

        String sql = "SELECT hdb.SoHoaDonBan, ct.MaQuanAo, hdb.MaKhachHang, hdb.MaNhanVien, ct.SoLuong, hdb.NgayBan, ct.ThanhTien " +
                     "FROM hoadonban hdb " +
                     "JOIN chitiethoadonban ct ON hdb.SoHoaDonBan = ct.SoHoaDonBan " +
                     "WHERE hdb.MaKhachHang = ?";

        try (Connection conn = ketnoiCSDL.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, maKH);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("SoHoaDonBan"),
                        rs.getInt("MaQuanAo"),
                        rs.getString("MaKhachHang"),
                        rs.getInt("MaNhanVien"),
                        rs.getInt("SoLuong"),
                        rs.getDate("NgayBan"),
                        rs.getDouble("ThanhTien")
                    };
                    model.addRow(row);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
