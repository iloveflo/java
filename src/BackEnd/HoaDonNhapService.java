package BackEnd;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Date;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import java.sql.Statement;
import java.text.SimpleDateFormat; 
import org.apache.poi.ss.usermodel.Row;
import java.awt.Desktop;


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
    public static DefaultTableModel timKiemHoaDonNhap(String soHoaDonNhap) {
        String query = "SELECT hd.SoHoaDonNhap, hd.MaNhanVien, ncc.TenNCC, ct.MaQuanAo, ct.SoLuong, ct.DonGia, ct.GiamGia, " +
                    "ct.SoLuong * ct.DonGia * (1 - ct.GiamGia / 100) AS ThanhTien " +
                    "FROM HoaDonNhap hd " +
                    "JOIN NhaCungCap ncc ON hd.MaNCC = ncc.MaNCC " +
                    "JOIN ChiTietHoaDonNhap ct ON hd.SoHoaDonNhap = ct.SoHoaDonNhap " +
                    "WHERE hd.SoHoaDonNhap LIKE ?";

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[] {
            "Số Hóa Đơn Nhập", "Mã Nhân Viên", "Tên Nhà Cung Cấp",
            "Mã Quần Áo", "Số Lượng", "Đơn Giá", "Giảm Giá", "Thành Tiền"
        });

        try (Connection conn = ketnoiCSDL.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + soHoaDonNhap + "%");

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

    public static void xuat(String soHoaDonNhap) {
        try (Connection conn = ketnoiCSDL.getConnection()) {

            // Truy vấn thông tin chung
            String sqlHeader = """
                SELECT hd.SoHoaDonNhap, hd.NgayNhap, hd.TongTien, nv.TenNhanVien
                FROM hoadonnhap hd
                JOIN nhanvien nv ON hd.MaNhanVien = nv.MaNhanVien
                WHERE hd.SoHoaDonNhap = ?
            """;

            PreparedStatement psHeader = conn.prepareStatement(sqlHeader);
            psHeader.setString(1, soHoaDonNhap);
            ResultSet rsHeader = psHeader.executeQuery();

            if (!rsHeader.next()) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy thông tin hóa đơn.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String tenNhanVien = rsHeader.getString("TenNhanVien");
            Date ngayNhap = rsHeader.getDate("NgayNhap");
            double tongTien = rsHeader.getDouble("TongTien");

            // Truy vấn chi tiết
            String sqlDetails = """
                SELECT 
                    sp.TenQuanAo AS `Tên quần áo`,
                    cthdn.SoLuong AS `Số lượng`,
                    cthdn.DonGia AS `Đơn giá`,
                    cthdn.GiamGia AS `Giảm giá (%)`,
                    cthdn.ThanhTien AS `Thành tiền`
                FROM chitiethoadonnhap cthdn
                JOIN sanpham sp ON cthdn.MaQuanAo = sp.MaQuanAo
                WHERE cthdn.SoHoaDonNhap = ?
            """;

            PreparedStatement psDetails = conn.prepareStatement(sqlDetails);
            psDetails.setString(1, soHoaDonNhap);
            ResultSet rsDetails = psDetails.executeQuery();

            // Ghi file Excel
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("HoaDonNhap");

            // Tiêu đề
            Row rowTitle = sheet.createRow(0);
            Cell titleCell = rowTitle.createCell(0);
            titleCell.setCellValue("HÓA ĐƠN NHẬP");
            CellStyle styleTitle = workbook.createCellStyle();
            Font fontTitle = workbook.createFont();
            fontTitle.setFontHeightInPoints((short) 16);
            fontTitle.setBold(true);
            styleTitle.setFont(fontTitle);
            titleCell.setCellStyle(styleTitle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

            // Thông tin chung
            String[][] info = {
                {"Số HĐ:", soHoaDonNhap},
                {"Ngày nhập:", new SimpleDateFormat("dd/MM/yyyy").format(ngayNhap)},
                {"Nhân viên:", tenNhanVien},
                {"Tổng tiền:", String.format("%,.0f đ", tongTien)}
            };

            for (int i = 0; i < info.length; i++) {
                Row row = sheet.createRow(2 + i);
                row.createCell(0).setCellValue(info[i][0]);
                row.createCell(1).setCellValue(info[i][1]);
            }

            // Header bảng chi tiết
            Row rowHeader = sheet.createRow(7);
            String[] columns = {"Tên quần áo", "Số lượng", "Đơn giá", "Giảm giá (%)", "Thành tiền"};
            for (int i = 0; i < columns.length; i++) {
                rowHeader.createCell(i).setCellValue(columns[i]);
            }

            int rowIndex = 8;
            while (rsDetails.next()) {
                Row row = sheet.createRow(rowIndex++);
                for (int i = 0; i < columns.length; i++) {
                    row.createCell(i).setCellValue(rsDetails.getString(i + 1));
                }
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ghi file
            String folderPath = "D:/ProjectJava";
            new File(folderPath).mkdirs();
            String filePath = folderPath + "/HoaDonNhap_" + soHoaDonNhap + ".xlsx";

            try (FileOutputStream out = new FileOutputStream(filePath)) {
                workbook.write(out);
            }
            workbook.close();

            Desktop.getDesktop().open(new File(filePath));
            JOptionPane.showMessageDialog(null, "Xuất hóa đơn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xuất hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void lapHoaDonNhap(
            String maquanao, String dongia, String giamgia, String soluongnhap,
            String manhanvien, String nhacungcap
    ) {
        try (Connection conn = ketnoiCSDL.getConnection()) {

            int maQuanAo = Integer.parseInt(maquanao);
            int maNhanVien = Integer.parseInt(manhanvien);
            int soLuongNhap = Integer.parseInt(soluongnhap);
            double donGiaNhap = Double.parseDouble(dongia);
            double giamGia = Double.parseDouble(giamgia);

            // Kiểm tra sản phẩm có tồn tại
            String checkSP = "SELECT COUNT(*) FROM SanPham WHERE MaQuanAo = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkSP)) {
                ps.setInt(1, maQuanAo);
                ResultSet rs = ps.executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(null, "Sản phẩm không tồn tại!");
                    return;
                }
            }

            // Lấy mã NCC
            int maNCC = -1;
            String getMaNCC = "SELECT MaNCC FROM NhaCungCap WHERE TenNCC = ?";
            try (PreparedStatement ps = conn.prepareStatement(getMaNCC)) {
                ps.setString(1, nhacungcap);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    maNCC = rs.getInt("MaNCC");
                } else {
                    JOptionPane.showMessageDialog(null, "Nhà cung cấp không tồn tại!");
                    return;
                }
            }

            // Cập nhật sản phẩm
            String updateSP = "UPDATE SanPham SET SoLuong = SoLuong + ?, DonGiaNhap = ? WHERE MaQuanAo = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSP)) {
                ps.setInt(1, soLuongNhap);
                ps.setDouble(2, donGiaNhap);
                ps.setInt(3, maQuanAo);
                ps.executeUpdate();
            }

            // Thêm Hóa đơn nhập
            int soHoaDonNhapMoi = -1;
            String insertHDN = "INSERT INTO HoaDonNhap (MaNhanVien, NgayNhap, MaNCC, TongTien) VALUES (?, NOW(), ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertHDN, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, maNhanVien);
                ps.setInt(2, maNCC);
                ps.setDouble(3, soLuongNhap * donGiaNhap);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    soHoaDonNhapMoi = rs.getInt(1);
                }
            }

            // Thêm chi tiết hóa đơn
            String insertCT = "INSERT INTO ChiTietHoaDonNhap (SoHoaDonNhap, MaQuanAo, SoLuong, DonGia, GiamGia, ThanhTien) " +
                              "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertCT)) {
                ps.setInt(1, soHoaDonNhapMoi);
                ps.setInt(2, maQuanAo);
                ps.setInt(3, soLuongNhap);
                ps.setDouble(4, donGiaNhap);
                ps.setDouble(5, giamGia);
                double thanhTien = soLuongNhap * donGiaNhap * (1 - giamGia / 100);
                ps.setDouble(6, thanhTien);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(null, "Thêm hóa đơn nhập thành công!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Lỗi: " + ex.getMessage());
        }
    }
}

