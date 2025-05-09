package BackEnd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ketnoiCSDL {
    // Thay localhost bằng IP của máy bạn
    private static final String URL = "jdbc:mysql://localhost:3306/ClothingStore?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";           // Hoặc user bạn cấp quyền
    private static final String PASSWORD = "binh11a10";  // Mật khẩu tương ứng

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

