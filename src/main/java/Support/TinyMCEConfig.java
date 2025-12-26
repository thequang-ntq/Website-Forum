package Support;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TinyMCEConfig {
    private static String TINYMCE_API_KEY;
    
    static {
        Properties config = new Properties();
        InputStream input = null;
        
        try {
            // Thử load từ classpath trước (file trong src/)
            input = TinyMCEConfig.class.getClassLoader()
                .getResourceAsStream("tinymce.properties");
            
            if (input == null) {
                // Nếu không tìm thấy, thử đường dẫn tương đối từ project root
                String projectPath = System.getProperty("user.dir");
                String filePath = projectPath + "/src/main/java/tinymce.properties";
                input = new FileInputStream(filePath);
            }
            
            if (input != null) {
                config.load(input);
                TINYMCE_API_KEY = config.getProperty("tinymce.apikey");
                System.out.println("TinyMCE configuration loaded successfully!");
            } else {
                System.err.println("Không tìm thấy file tinymce.properties!");
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file tinymce.properties: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static String getApiKey() {
        if (TINYMCE_API_KEY == null || TINYMCE_API_KEY.trim().isEmpty()) {
            System.err.println("TinyMCE API Key chưa được cấu hình!");
            return "";
        }
        return TINYMCE_API_KEY;
    }
}