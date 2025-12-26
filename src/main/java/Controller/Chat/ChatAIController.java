package Controller.Chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import Modal.BaiViet.BaiViet;
import Modal.BaiViet.BaiVietBO;
import Modal.BinhLuan.BinhLuan;
import Modal.BinhLuan.BinhLuanBO;
import Modal.DoanChat.DoanChatBO;
import Modal.TinNhanChat.TinNhanChat;
import Modal.TinNhanChat.TinNhanChatBO;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Support.OpenAIChatService;

@WebServlet("/ChatAIController")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024) // 5MB
public class ChatAIController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final OpenAIChatService chatService;
    private final Gson gson;
    private final SimpleDateFormat sdf;
	private BaiVietBO bvbo = new BaiVietBO();
    private BinhLuanBO blbo = new BinhLuanBO();
    private DoanChatBO doanChatBO = new DoanChatBO();
    private TinNhanChatBO tinNhanBO = new TinNhanChatBO();

    public ChatAIController() {
        super();
        this.chatService = new OpenAIChatService();
        this.gson = new Gson();
        this.sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    }

    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        HttpSession session = request.getSession(false);
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        // THÊM ĐOẠN NÀY - Kiểm tra session trước khi xử lý
        if (session == null || session.getAttribute("account") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.addProperty("success", false);
            result.addProperty("message", "Phiên đăng nhập hết hạn. Vui lòng tải lại trang.");
            out.print(result.toString());
            return;
        }

        try {
            String message = request.getParameter("message");
            Part imagePart = request.getPart("image");
            String maDoanChatParam = request.getParameter("maDoanChat");
            
            // Kiểm tra tin nhắn không rỗng
            if ((message == null || message.trim().isEmpty()) && imagePart == null) {
                result.addProperty("success", false);
                result.addProperty("message", "Tin nhắn không được để trống");
                out.print(result.toString());
                return;
            }

            String account = (String) session.getAttribute("account");
            
            long maDoanChat;
            
            // Tạo đoạn chat mới nếu chưa có
            if (maDoanChatParam == null || maDoanChatParam.trim().isEmpty()) {
                String tieuDe = (message != null && !message.trim().isEmpty()) 
                    ? (message.length() > 50 ? message.substring(0, 50) + "..." : message)
                    : "Chat mới";
                maDoanChat = doanChatBO.createDB(account, tieuDe);
            } 
            else {
                maDoanChat = Long.parseLong(maDoanChatParam);
            }

            // Upload ảnh nếu có
            String imageUrl = null;
            Part imagePartForAPI = null;

            // Upload ảnh và lưu URL vào DB
            if (imagePart != null && imagePart.getSize() > 0) {
                imageUrl = uploadImage(imagePart, request);
                // Reset lại stream để có thể đọc lại cho API
                imagePartForAPI = request.getPart("image");
            }

            // Lưu tin nhắn user, tin nhắn hỏi của người dùng
            tinNhanBO.createDB(maDoanChat, "user", message != null ? message : "", imageUrl);

            // Lấy lịch sử chat từ DB
            ArrayList<TinNhanChat> chatHistory = tinNhanBO.readDB(maDoanChat);
            ArrayList<Map<String, Object>> historyForAPI = new ArrayList<>();

            for (TinNhanChat tn : chatHistory) {
                Map<String, Object> msg = new HashMap<>();
                msg.put("role", tn.getRole());
                msg.put("content", tn.getNoiDung());
                msg.put("hasImage", tn.getUrl() != null && !tn.getUrl().trim().isEmpty());
                historyForAPI.add(msg);
            }

            // Gọi OpenAI API với Part mới
            String aiResponse = chatService.sendMessage(message, imagePartForAPI, historyForAPI);

            // Lưu response của AI
            tinNhanBO.createDB(maDoanChat, "assistant", aiResponse, null);

            // Cập nhật thời gian cập nhật sau khi lưu tin nhắn hỏi và trả lời của user và AI Chatbot
            doanChatBO.updateDB(maDoanChat);
            
            // Sau khi có imageUrl và đã lưu tin nhắn user
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                String currentFileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                // Gọi cleanup nhưng truyền thêm filename để bảo vệ
                cleanOrphanChatImages(request, currentFileName);
            }
            else {
                cleanOrphanChatImages(request, null);
            }
            
            // Dọn dẹp file ảnh dư thừa

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String timestamp = sdf.format(new Date());

            result.addProperty("success", true);
            result.addProperty("response", aiResponse);
            result.addProperty("timestamp", timestamp);
            result.addProperty("maDoanChat", maDoanChat);
            result.addProperty("imageUrl", imageUrl);

        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage();
            if (errorMsg == null || errorMsg.isEmpty()) {
                errorMsg = "Đã xảy ra lỗi không xác định.";
            }
            result.addProperty("success", false);
            result.addProperty("message", errorMsg);
        }

        out.print(result.toString());
        out.flush();
    }

    // Hàm xử lý ảnh tương tự như xử lý ảnh bên bài viết và bình luận
    private String uploadImage(Part imagePart, HttpServletRequest request) throws Exception {
        String fileName = getFileName(imagePart);
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        
        // Lưu ở trên Server và ở trên storage trong local nếu như Server là .metadata, tức là đang chạy local
        String uploadPath = request.getServletContext().getRealPath("") + "storage";
        String uploadPath2 = null;
        
        if (uploadPath.contains(".metadata") && System.getProperty("os.name").toLowerCase().contains("win")) {
            uploadPath2 = "D:/Nam4/JavaNangCao" + request.getContextPath() + "/src/main/webapp/storage";
        }
        
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();
        
        Path filePath = Paths.get(uploadPath, uniqueFileName);
        Files.copy(imagePart.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        if (uploadPath2 != null) {
            File uploadDir2 = new File(uploadPath2);
            if (!uploadDir2.exists()) uploadDir2.mkdirs();
            Path filePath2 = Paths.get(uploadPath2, uniqueFileName);
            Files.copy(imagePart.getInputStream(), filePath2, StandardCopyOption.REPLACE_EXISTING);
        }
        
        return "storage/" + uniqueFileName;
    }

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
    
    /**
     * Dọn dẹp file ảnh chat orphan: So sánh file trong storage với URL trong DB TinNhanChat, xóa file không dùng.
     */
    private void cleanOrphanChatImages(HttpServletRequest request, String protectFileName) {
        try {
        	// Bước 1: Lấy set tên file từ DB (chỉ phần filename từ URL) từ cả bài viết và bình luận, và tin nhắn chat
            Set<String> usedFileNames = new HashSet<>();
            // Bài viết
            for (BaiViet bv : bvbo.readDB()) {
                String url = bv.getUrl();
                if (url != null && !url.trim().isEmpty()) {
                    // Trích xuất filename từ URL (ví dụ: "storage/abc123.jpg" -> "abc123.jpg")
                    String fileName = url.substring(url.lastIndexOf("/") + 1);
                    if (!fileName.isEmpty()) {
                        usedFileNames.add(fileName);
                    }
                }
            }
            
            // Bình luận
            for (BinhLuan bl : blbo.readDB()) {
                String url = bl.getUrl();
                if (url != null && !url.trim().isEmpty()) {
                    // Trích xuất filename từ URL (ví dụ: "storage/abc123.jpg" -> "abc123.jpg")
                    String fileName = url.substring(url.lastIndexOf("/") + 1);
                    if (!fileName.isEmpty()) {
                        usedFileNames.add(fileName);
                    }
                }
            }
            
            // Tin nhắn chat
            for (TinNhanChat tn : tinNhanBO.readDB2()) {
                String url = tn.getUrl();
                if (url != null && !url.trim().isEmpty()) {
                    // Trích xuất filename từ URL (ví dụ: "storage/abc123.jpg" -> "abc123.jpg")
                    String fileName = url.substring(url.lastIndexOf("/") + 1);
                    if (!fileName.isEmpty()) {
                        usedFileNames.add(fileName);
                    }
                }
            }
            
            // Bảo vệ file hiện tại (nếu có)
            if (protectFileName != null && !protectFileName.isEmpty()) {
                usedFileNames.add(protectFileName);
            }

            // Bước 2: Lấy đường dẫn storage
            String uploadPath = request.getServletContext().getRealPath("") + "storage"; // Server
            String uploadPath2 = null;
            if (uploadPath.contains(".metadata") && System.getProperty("os.name").toLowerCase().contains("win")) {
                uploadPath2 = "D:/Nam4/JavaNangCao" + request.getContextPath() + "/src/main/webapp/storage"; // Local
            }

            // Bước 3: Duyệt và xóa orphan ở server
            File serverDir = new File(uploadPath);
            if (serverDir.exists() && serverDir.isDirectory()) {
                File[] serverFiles = serverDir.listFiles();
                if (serverFiles != null) {
                    for (File file : serverFiles) {
                        if (file.isFile() && !usedFileNames.contains(file.getName())) {
                            // Xóa orphan
                            if (file.delete()) {
                                System.out.println("Đã xóa file ảnh chat orphan trên server: " + file.getName());
                            }
                        }
                    }
                }
            }

            // Bước 4: Duyệt và xóa orphan ở local (nếu tồn tại)
            if (uploadPath2 != null) {
                File localDir = new File(uploadPath2);
                if (localDir.exists() && localDir.isDirectory()) {
                    File[] localFiles = localDir.listFiles();
                    if (localFiles != null) {
                        for (File file : localFiles) {
                            if (file.isFile() && !usedFileNames.contains(file.getName())) {
                                // Xóa orphan
                                if (file.delete()) {
                                    System.out.println("Đã xóa file ảnh chat orphan trên local: " + file.getName());
                                }
                            }
                        }
                    }
                }
            }
        } 
        catch (Exception e) {
            System.err.println("Lỗi khi dọn dẹp file ảnh chat orphan: " + e.getMessage());
            e.printStackTrace();
        }
    }
}