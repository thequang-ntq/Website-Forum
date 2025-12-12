package Controller.Chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

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

        HttpSession session = request.getSession(false); // không tạo mới nếu chưa có
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        // Kiểm tra đăng nhập
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

            if ((message == null || message.trim().isEmpty()) && imagePart == null) {
                result.addProperty("success", false);
                result.addProperty("message", "Tin nhắn không được để trống");
                out.print(result.toString());
                return;
            }

            // Lấy lịch sử chat từ session
            ArrayList<Map<String, Object>> chatHistory = 
                (ArrayList<Map<String, Object>>) session.getAttribute("chatHistory");

            if (chatHistory == null) {
                chatHistory = new ArrayList<>();
                session.setAttribute("chatHistory", chatHistory);
            }

            // Gọi OpenAI API
            String aiResponse = chatService.sendMessage(message, imagePart, chatHistory);

            String timestamp = sdf.format(new Date());

            // === LƯU TIN NHẮN NGƯỜI DÙNG ===
            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("timestamp", timestamp);

            if (imagePart != null) {
                // Có ảnh → lưu base64 + text (nếu có)
                InputStream is = imagePart.getInputStream();
                byte[] bytes = is.readAllBytes();
                String base64 = Base64.getEncoder().encodeToString(bytes);

                // Nếu có cả text + ảnh → lưu dạng: "text|||base64"
                // Nếu chỉ có ảnh → chỉ lưu base64
                String contentToSave = (message != null && !message.trim().isEmpty())
                        ? message.trim() + "|||IMAGE_BASE64|||" + base64
                        : base64;

                userMessage.put("content", contentToSave);
                userMessage.put("hasImage", true);
            } else {
                userMessage.put("content", message.trim());
                userMessage.put("hasImage", false);
            }

            // === LƯU TIN NHẮN TRỢ LÝ ===
            Map<String, Object> assistantMessage = new HashMap<>();
            assistantMessage.put("role", "assistant");
            assistantMessage.put("content", aiResponse);
            assistantMessage.put("timestamp", timestamp);
            assistantMessage.put("hasImage", false);

            // Thêm vào lịch sử
            chatHistory.add(userMessage);
            chatHistory.add(assistantMessage);

            // Trả về JSON cho frontend (chỉ cần response + timestamp)
            result.addProperty("success", true);
            result.addProperty("response", aiResponse);
            result.addProperty("timestamp", timestamp);

        } catch (Exception e) {
            e.printStackTrace();
            result.addProperty("success", false);
            result.addProperty("message", "Lỗi server: " + e.getMessage());
        }

        out.print(result.toString());
        out.flush();
    }
}