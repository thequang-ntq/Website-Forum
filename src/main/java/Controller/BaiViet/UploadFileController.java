package Controller.BaiViet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/UploadFileController")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 50,      // 50MB
    maxRequestSize = 1024 * 1024 * 100   // 100MB
)
public class UploadFileController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Các định dạng file được phép
    private static final String[] ALLOWED_IMAGE_TYPES = {
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    };
    
    private static final String[] ALLOWED_VIDEO_TYPES = {
        "video/mp4", "video/webm", "video/ogg"
    };
    
    public UploadFileController() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        
        try {
            // Lấy file từ request
            Part filePart = request.getPart("file");
            
            if (filePart == null || filePart.getSize() == 0) {
                response.getWriter().write("{\"success\": false, \"message\": \"Vui lòng chọn file!\"}");
                return;
            }
            
            // Kiểm tra loại file
            String contentType = filePart.getContentType();
            if (!isValidFileType(contentType)) {
                response.getWriter().write("{\"success\": false, \"message\": \"Định dạng file không được hỗ trợ!\"}");
                return;
            }
            
            // Lấy tên file gốc
            String fileName = getFileName(filePart);
            
            // Tạo tên file mới (unique) để tránh trùng lặp
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            
            // Đường dẫn thư mục storage
            String uploadPath = request.getServletContext().getRealPath("") + File.separator + "storage";
            if (uploadPath.contains(".metadata") && System.getProperty("os.name").toLowerCase().contains("win")) {
                uploadPath = "D:\\Nam4\\JavaNangCao\\WebsiteForum\\src\\main\\webapp\\storage";
            }

            
            // Tạo thư mục nếu chưa tồn tại
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // Đường dẫn file đầy đủ
            Path filePath = Paths.get(uploadPath, uniqueFileName);
            
            // Lưu file
            Files.copy(filePart.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Trả về đường dẫn tương đối
            String relativeUrl = "storage/" + uniqueFileName;
            
            response.getWriter().write("{\"success\": true, \"url\": \"" + relativeUrl + "\", \"fileName\": \"" + fileName + "\"}");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"success\": false, \"message\": \"Lỗi khi tải file: " + e.getMessage() + "\"}");
        }
    }
    
    // Kiểm tra loại file hợp lệ
    private boolean isValidFileType(String contentType) {
        for (String type : ALLOWED_IMAGE_TYPES) {
            if (type.equals(contentType)) {
                return true;
            }
        }
        for (String type : ALLOWED_VIDEO_TYPES) {
            if (type.equals(contentType)) {
                return true;
            }
        }
        return false;
    }
    
    // Lấy tên file từ Part
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] tokens = contentDisposition.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "";
    }
}