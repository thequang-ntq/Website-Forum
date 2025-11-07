package Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConfig {
    // Thông tin kết nối SQL Server
    private final String url = "jdbc:sqlserver://DESKTOP-GAFBKMF:1433;databaseName=22T1020362_Forum;encrypt=false;trustServerCertificate=true";
    private final String user = "sa";
    private final String pass = "sa";
    private Connection cn;

    public Connection getCn() {
        return this.cn;
    }

    // 1. Private constructor (Singleton)
    private DBConfig() {
        try {
            // Nạp driver của SQL Server
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            // Kết nối
            cn = DriverManager.getConnection(url, user, pass);
            System.out.println("Kết nối SQL Server thành công!");
        } catch (ClassNotFoundException e) {
            System.out.println("Không tìm thấy driver SQL Server. Hãy đảm bảo đã thêm mssql-jdbc-*.jar vào WEB-INF/lib");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Kết nối SQL Server thất bại. Kiểm tra lại thông tin cấu hình!");
            e.printStackTrace();
        }
    }

    // 2. Instance duy nhất
    private static DBConfig instance = null;

    // 3. Hàm getInstance()
    public static DBConfig getInstance() {
        if (instance == null) {
            instance = new DBConfig();
        }
        return instance;
    }
}

