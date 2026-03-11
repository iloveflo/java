# 👕 CỬA HÀNG QUẦN ÁO BDTHD

> **BDTHD – Tôn vinh phong cách, khẳng định cá tính**

Ứng dụng quản lý cửa hàng quần áo được xây dựng bằng **Java Swing**, kết nối cơ sở dữ liệu **MySQL**, hỗ trợ đầy đủ các nghiệp vụ bán hàng, quản lý kho, nhân viên và khách hàng.

---

## 📋 Mục lục

- [Tính năng](#-tính-năng)
- [Công nghệ sử dụng](#-công-nghệ-sử-dụng)
- [Cấu trúc dự án](#-cấu-trúc-dự-án)
- [Yêu cầu hệ thống](#-yêu-cầu-hệ-thống)
- [Hướng dẫn cài đặt](#-hướng-dẫn-cài-đặt)
- [Phân quyền người dùng](#-phân-quyền-người-dùng)
- [Thư viện bên thứ ba](#-thư-viện-bên-thứ-ba)
- [Tác giả](#-tác-giả)

---

## ✨ Tính năng

### 🔐 Xác thực & Bảo mật
- Đăng nhập / Đăng ký tài khoản
- CAPTCHA xác minh khi đăng nhập
- Xác thực OTP qua email (Jakarta Mail)
- Quên mật khẩu & Đổi mật khẩu
- Quản lý phiên đăng nhập (ngăn đăng nhập trùng thiết bị)

### 👗 Quản lý sản phẩm
- Thêm / Sửa / Xóa sản phẩm quần áo
- Lọc sản phẩm theo: thể loại, kích cỡ, chất liệu, màu sắc, đối tượng, mùa, nơi sản xuất
- Hiển thị hình ảnh sản phẩm
- Danh sách mặt hàng bán chạy & sắp hết hàng

### 🛒 Mua hàng & Đơn hàng
- Giỏ hàng cho khách hàng
- Đặt hàng trực tuyến
- Hướng dẫn chọn size & đặt hàng

### 📄 Hóa đơn
- Hóa đơn bán hàng
- Hóa đơn nhập hàng (Admin & Nhân viên)
- Lập hóa đơn trực tiếp

### 👥 Quản lý con người
- Quản lý thông tin khách hàng
- Quản lý thông tin nhân viên
- Thông tin tài khoản cá nhân

### 📊 Thống kê & Báo cáo
- Thống kê doanh thu theo tháng
- Xuất báo cáo ra file **Excel (.xlsx)** với Apache POI

### 📞 Hỗ trợ
- Trang liên hệ & hỗ trợ khách hàng

---

## 🛠 Công nghệ sử dụng

| Công nghệ | Mô tả |
|---|---|
| **Java SE** | Ngôn ngữ lập trình chính |
| **Java Swing** | Xây dựng giao diện người dùng (GUI) |
| **MySQL** | Hệ quản trị cơ sở dữ liệu |
| **JDBC** | Kết nối ứng dụng với database |
| **Apache POI** | Xuất báo cáo ra file Excel |
| **Jakarta Mail** | Gửi email xác thực OTP |

---

## 📁 Cấu trúc dự án

```
java/
├── src/
│   ├── Main.java                  # Điểm khởi chạy ứng dụng
│   ├── BackEnd/                   # Xử lý logic nghiệp vụ & truy vấn CSDL
│   │   ├── ketnoiCSDL.java        # Kết nối MySQL
│   │   ├── AuthService.java       # Đăng nhập, CAPTCHA
│   │   ├── RegisterService.java   # Đăng ký tài khoản
│   │   ├── SessionManager.java    # Quản lý phiên đăng nhập
│   │   ├── OTPService.java        # Xác thực OTP qua email
│   │   ├── Sanphamdata.java       # CRUD sản phẩm
│   │   ├── GioHangService.java    # Giỏ hàng
│   │   ├── DatHangProcessor.java  # Xử lý đặt hàng
│   │   ├── DonHangDAO.java        # Truy vấn đơn hàng
│   │   ├── HoaDonBanService.java  # Hóa đơn bán
│   │   ├── HoaDonNhapService.java # Hóa đơn nhập
│   │   ├── KhachhangData.java     # Dữ liệu khách hàng
│   │   ├── NhanvienData.java      # Dữ liệu nhân viên
│   │   ├── ThongkeService.java    # Thống kê & xuất Excel
│   │   ├── DoiMatKhauService.java # Đổi mật khẩu
│   │   └── QuenMatKhauService.java# Quên mật khẩu
│   └── GUI/                       # Giao diện người dùng (Swing)
│       ├── Start.java             # Màn hình đăng nhập
│       ├── register.java          # Màn hình đăng ký
│       ├── Menu.java              # Menu chính (Admin)
│       ├── Sanpham.java           # Quản lý sản phẩm (Admin)
│       ├── SanphamKH.java         # Xem sản phẩm (Khách hàng)
│       ├── Giohang.java           # Giỏ hàng
│       ├── Khachhangdathang.java  # Đặt hàng
│       ├── Hoadonban.java         # Hóa đơn bán
│       ├── Hoadonnhap.java        # Hóa đơn nhập
│       ├── Khachhang.java         # Quản lý khách hàng
│       ├── Nhanvien.java          # Quản lý nhân viên
│       ├── Thongke.java           # Thống kê doanh thu
│       ├── Thongtintk.java        # Thông tin tài khoản
│       ├── Doimatkhau.java        # Đổi mật khẩu
│       ├── Quenmatkhau.java       # Quên mật khẩu
│       ├── OTP.java / OTP1.java   # Xác thực OTP
│       ├── Mathangbanchay.java    # Mặt hàng bán chạy
│       ├── Mathangsaphet.java     # Mặt hàng sắp hết
│       ├── Lienhehotro.java       # Liên hệ hỗ trợ
│       └── icons/                 # Tài nguyên hình ảnh
├── lib/                           # Thư viện JAR bên thứ ba
├── bin/                           # File .class đã biên dịch
└── README.md
```

---

## 💻 Yêu cầu hệ thống

- **Java JDK** 11 trở lên
- **MySQL Server** 5.7 trở lên
- IDE hỗ trợ Java (VSCode, IntelliJ IDEA, NetBeans, Eclipse,...)

---

## 🚀 Hướng dẫn cài đặt

### 1. Clone dự án

```bash
git clone https://github.com/iloveflo/ClothingStore-BDTHD.git
cd ClothingStore-BDTHD
```

### 2. Thiết lập cơ sở dữ liệu

- Cài đặt và khởi động **MySQL Server**
- Tạo database tên `ClothingStore`
- Import file SQL (nếu có) để tạo các bảng cần thiết

### 3. Cấu hình kết nối

Mở file `src/BackEnd/ketnoiCSDL.java` và cập nhật thông tin kết nối:

```java
private static final String URL = "jdbc:mysql://localhost:3306/ClothingStore";
private static final String USER = "root";
private static final String PASSWORD = "your_password";
```

### 4. Chạy ứng dụng

Biên dịch và chạy file `Main.java`:

```bash
# Biên dịch
javac -cp "lib/*" -d bin src/Main.java src/BackEnd/*.java src/GUI/*.java

# Chạy
java -cp "bin:lib/*" Main
```

> **Lưu ý:** Trên Windows, thay dấu `:` bằng `;` trong classpath:
> ```bash
> java -cp "bin;lib/*" Main
> ```

---

## 👤 Phân quyền người dùng

| Vai trò | Quyền hạn |
|---|---|
| **Admin** | Toàn quyền: quản lý sản phẩm, nhân viên, khách hàng, hóa đơn, thống kê, xuất báo cáo |
| **Nhân viên (NhanVien)** | Xem sản phẩm, lập hóa đơn nhập/bán, quản lý thông tin cá nhân |
| **Khách hàng (KhachHang)** | Xem sản phẩm, thêm giỏ hàng, đặt hàng, xem hóa đơn cá nhân |

---

## 📦 Thư viện bên thứ ba

| Thư viện | Phiên bản | Mục đích |
|---|---|---|
| MySQL Connector/J | 9.2.0 | Kết nối MySQL |
| MSSQL JDBC | 12.10.0 | Kết nối SQL Server (dự phòng) |
| Apache POI | 5.2.3 | Đọc/ghi file Excel (.xlsx) |
| Jakarta Mail | 2.0.1 | Gửi email OTP |
| Commons IO | 2.11.0 | Tiện ích xử lý I/O |
| Commons Codec | 1.15 | Mã hóa dữ liệu |
| Log4j | 2.18.0 | Ghi log ứng dụng |

---

## 🗄️ Cơ sở dữ liệu

Database sử dụng MySQL với các bảng chính:

- `TaiKhoan` – Tài khoản đăng nhập
- `sanpham` – Sản phẩm quần áo
- `theloai`, `co`, `chatlieu`, `mau`, `doituong`, `mua`, `noisanxuat` – Bảng danh mục
- `CapCha` – Mã CAPTCHA
- Các bảng hóa đơn, đơn hàng, khách hàng, nhân viên...

---

## 👨‍💻 Tác giả

- **BDTHD Team**

---

## 📄 Giấy phép

Dự án được phát triển cho mục đích học tập.

---

<p align="center">
  <b>⭐ Nếu thấy hữu ích, hãy cho dự án một ngôi sao trên GitHub! ⭐</b>
</p>