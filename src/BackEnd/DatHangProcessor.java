package BackEnd;

import java.sql.*;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.text.SimpleDateFormat;
import javax.swing.*;
import java.math.BigDecimal;


public class DatHangProcessor {
    public static boolean datHang(String maKhachHang) {
        try (Connection conn = ketnoiCSDL.getConnection()) {

            // Kiểm tra giỏ hàng
            String checkQuery = "SELECT COUNT(*) FROM GioHang WHERE MaKhachHang = ?";
            try (PreparedStatement checkCmd = conn.prepareStatement(checkQuery)) {
                checkCmd.setString(1, maKhachHang);
                ResultSet rs = checkCmd.executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(null, "Giỏ hàng của bạn đang trống!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }

            // Xác nhận người dùng
            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn đặt toàn bộ hàng trong giỏ?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return false;
            // Cập nhật trạng thái giỏ hàng sang "đã đặt" (TrangThai = 1)
            
            String updateTrangThaiQuery = "UPDATE GioHang SET TrangThai = 1 WHERE MaKhachHang = ?";
            try (PreparedStatement updateCmd = conn.prepareStatement(updateTrangThaiQuery)) {
                updateCmd.setString(1, maKhachHang);
                updateCmd.executeUpdate();
            }


            // Lấy email & tên khách
            String email = "", tenKhach = "";
            String emailQuery = "SELECT Email, TenKhach FROM khachhang WHERE MaTaiKhoan = ?";
            try (PreparedStatement stmt = conn.prepareStatement(emailQuery)) {
                stmt.setString(1, maKhachHang);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    email = rs.getString("Email");
                    tenKhach = rs.getString("TenKhach");
                }
            }

            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy email khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Tạo nội dung đơn hàng
            StringBuilder donHang = new StringBuilder();
            donHang.append("ĐƠN HÀNG CỦA ").append(tenKhach).append(" - ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date())).append("\n\n");
            donHang.append(String.format("%-30s%-15s%-15s%-15s%-15s%-15s%-15s%-15s%-15s%-15s%-15s\n", "Tên", "Loại", "Size", "Màu", "Mùa", "Đối tượng", "Chất liệu", "NSX", "SL", "Đơn giá", "Thành tiền"));
            donHang.append("-".repeat(180)).append("\n");

            String gioQuery = """
                SELECT s.TenQuanAo, t.TenLoai, c.TenCo, m.TenMau, mu.TenMua, dt.TenDoiTuong,
                       cl.TenChatLieu, ns.TenNSX, gh.SoLuongDat, gh.DonGiaBan, gh.TongTien
                FROM GioHang gh
                JOIN SanPham s ON gh.MaQuanAo = s.MaQuanAo
                JOIN TheLoai t ON s.MaLoai = t.MaLoai
                JOIN Co c ON s.MaCo = c.MaCo
                JOIN Mau m ON s.MaMau = m.MaMau
                JOIN Mua mu ON s.MaMua = mu.MaMua
                JOIN DoiTuong dt ON s.MaDoiTuong = dt.MaDoiTuong
                JOIN ChatLieu cl ON s.MaChatLieu = cl.MaChatLieu
                JOIN NoiSanXuat ns ON s.MaNSX = ns.MaNSX
                WHERE gh.MaKhachHang = ?""";

            BigDecimal tongTien = BigDecimal.ZERO;
            try (PreparedStatement gioCmd = conn.prepareStatement(gioQuery)) {
                gioCmd.setString(1, maKhachHang);
                ResultSet rs = gioCmd.executeQuery();
                while (rs.next()) {
                    String row = String.format("%-30s%-15s%-15s%-15s%-15s%-15s%-15s%-15s%-15d%-15s%-15s\n",
                        rs.getString("TenQuanAo"), rs.getString("TenLoai"), rs.getString("TenCo"),
                        rs.getString("TenMau"), rs.getString("TenMua"), rs.getString("TenDoiTuong"),
                        rs.getString("TenChatLieu"), rs.getString("TenNSX"),
                        rs.getInt("SoLuongDat"),
                        rs.getBigDecimal("DonGiaBan").toString(),
                        rs.getBigDecimal("TongTien").toString());
                    donHang.append(row);
                    tongTien = tongTien.add(rs.getBigDecimal("TongTien"));
                }
            }

            donHang.append("\nTổng Tiền Thanh Toán: ").append(tongTien.toString());
            donHang.append("\n\nCó thể chuyển khoản theo thông tin bên dưới:\nMã QR để chuyển khoản: https://drive.google.com/file/d/1q-FZNGdZx7DHkidkY199QYu2F0t8Jxje/view?usp=sharing");

            // Gửi email
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            jakarta.mail.Session session = jakarta.mail.Session.getInstance(props, new jakarta.mail.Authenticator() {
                @Override
                protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new jakarta.mail.PasswordAuthentication("binha10k56@gmail.com", "");
                }
            });
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("binha10k56@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Xác nhận đơn hàng - ClothingStore");
            message.setText(donHang.toString());

            Transport.send(message);
            JOptionPane.showMessageDialog(null, "Đặt hàng thành công! Thông tin đơn hàng đã được gửi tới Email:"+email, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi đặt hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}

