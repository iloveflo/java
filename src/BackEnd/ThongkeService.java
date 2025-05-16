package BackEnd;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;

public class ThongkeService {
    public static void xuatBaoCao(JTable tblThongke, String filePath) throws Exception {
        if (tblThongke.getRowCount() == 0) {
            throw new Exception("Không có dữ liệu để xuất!");
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("BáoCáo");

        // Tiêu đề lớn
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        String thangNam = tblThongke.getValueAt(0, 0).toString(); // Ví dụ: "5/2024"
        titleCell.setCellValue("BÁO CÁO DOANH THU THÁNG " + thangNam + " – BDTHD");

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        CellStyle titleStyle = workbook.createCellStyle();

        org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();

        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 12);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCell.setCellStyle(titleStyle);

        // Dữ liệu chi tiết
        String[] nhan = {"Tổng Số lượng Bán:", "Tổng Doanh Thu:", "Tổng Lợi Nhuận:"};
        Object[] data = {
            tblThongke.getValueAt(0, 1),
            tblThongke.getValueAt(0, 2) + " VNĐ",
            tblThongke.getValueAt(0, 3) + " VNĐ"
        };

        for (int i = 0; i < nhan.length; i++) {
            Row row = sheet.createRow(i + 2);
            Cell cellLabel = row.createCell(0);
            Cell cellValue = row.createCell(1);
            cellLabel.setCellValue(nhan[i]);
            cellValue.setCellValue(data[i].toString());

            CellStyle borderStyle = workbook.createCellStyle();
            borderStyle.setBorderTop(BorderStyle.THIN);
            borderStyle.setBorderBottom(BorderStyle.THIN);
            borderStyle.setBorderLeft(BorderStyle.THIN);
            borderStyle.setBorderRight(BorderStyle.THIN);

            org.apache.poi.ss.usermodel.Font boldFont = workbook.createFont();

            boldFont.setBold(true);
            borderStyle.setFont(boldFont);

            cellLabel.setCellStyle(borderStyle);
            cellValue.setCellStyle(borderStyle);
        }

        for (int i = 0; i <= 2; i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream out = new FileOutputStream(filePath);
        workbook.write(out);
        out.close();
        workbook.close();

        Desktop.getDesktop().open(new File(filePath));
    }

}
