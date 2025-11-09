package Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OldConfig {
    // Thông tin kết nối MySQL
	private final String url = "jdbc:mysql://localhost:3306/22t1020362_forum?useUnicode=true&serverTimezone=Asia/Ho_Chi_Minh";
    private final String user = "root";
    private final String pass = "quang";
    private Connection cn;
    
    public Connection getCn() {
        return this.cn;
    }
    
    // 1. Private constructor (Singleton)
    private OldConfig() {
        try {
            // Nạp driver của MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Kết nối
            cn = DriverManager.getConnection(url, user, pass);
            System.out.println("Kết nối MySQL thành công!");
        } catch (ClassNotFoundException e) {
            System.out.println("Không tìm thấy driver MySQL. Hãy đảm bảo đã thêm mysql-connector-j-9.5.0.jar vào thư viện");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Kết nối MySQL thất bại. Kiểm tra lại thông tin cấu hình!");
            e.printStackTrace();
        }
    }
    
    // 2. Instance duy nhất
    private static OldConfig instance = null;
    
    // 3. Hàm getInstance()
    public static OldConfig getInstance() {
        if (instance == null) {
            instance = new OldConfig();
        }
        return instance;
    }
    
    // 4. Đóng kết nối (khuyến nghị thêm)
    public void closeConnection() {
        try {
            if (cn != null && !cn.isClosed()) {
                cn.close();
                System.out.println("Đã đóng kết nối MySQL!");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi đóng kết nối!");
            e.printStackTrace();
        }
    }
}


