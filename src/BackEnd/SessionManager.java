package BackEnd;

public class SessionManager {
    private static String maTaiKhoan = "";
    private static String loaiTaiKhoan = "";

    public static void setSession(String ma, String loai) {
        maTaiKhoan = ma;
        loaiTaiKhoan = loai;
    }

    public static String getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public static String getLoaiTaiKhoan() {
        return loaiTaiKhoan;
    }

    public static void clearSession() {
        maTaiKhoan = "";
        loaiTaiKhoan = "";
    }
}

