package Support;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import Modal.ApiKey.ApiKeyBO;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GeminiSearchService {
    private static ApiKeyBO apikeybo;
    private static String API_KEY;
    private static final String API_URL_TEMPLATE = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=%s";

    private final OkHttpClient client;
    private final Gson gson;
    private final String apiUrl;

    public GeminiSearchService() {
        this.client = OkHttpClientManager.getClient();
        this.gson = new Gson();
        this.apiUrl = buildApiUrl(); // Gọi ở đây để đảm bảo API_KEY đã sẵn sàng
    }
    
    private static synchronized String getApiKey() {
        if (API_KEY == null) {
        	try {
        		apikeybo = new ApiKeyBO();
                API_KEY = apikeybo.getApiKey();
                if (API_KEY == null || API_KEY.trim().isEmpty()) {
                    throw new IllegalStateException("API Key không được cấu hình! Kiểm tra ApiKeyBO.");
                }
        	}
        	catch (Exception e) {
        		e.printStackTrace();
        	}
            
        }
        return API_KEY;
    }
    
    private String buildApiUrl() {
        return String.format(API_URL_TEMPLATE, getApiKey());
    }
    
    public String enhanceSearchQuery(String userQuery, String context) {
        try {
            String prompt = buildPrompt(userQuery, context);
            String response = callGeminiAPI(prompt);
            return parseResponse(response, userQuery);
        } catch (Exception e) {
        	
            System.err.println("Gemini API error: " + e.getMessage());
            return userQuery; // Fallback to original query
        }
    }
    
    private String buildPrompt(String query, String context) {
        return String.format(
            "Bạn là công cụ tìm kiếm thông minh cho diễn đàn bài viết.\n" +
            "Ngữ cảnh tìm kiếm: %s (chủ yếu tìm trong tiêu đề, nội dung bài viết và bình luận).\n" +
            "Truy vấn người dùng: '%s'\n" +
            "Nhiệm vụ: Mở rộng và trích xuất từ khóa tối ưu để tìm bài viết liên quan nhất.\n" +
            "- Hiểu ý định người dùng (ví dụ: 'video' có thể nghĩa là bài có video, bài nói về video, hoặc chứa từ video).\n" +
            "- Thêm từ khóa đồng nghĩa, liên quan bằng tiếng Việt nếu cần (ví dụ: video → video phim clip mp4).\n" +
            "- Loại bỏ từ dừng không cần thiết.\n" +
            "- Ưu tiên từ khóa cụ thể để kết quả chính xác, tránh quá rộng.\n" +
            "Chỉ trả về danh sách từ khóa cách nhau bởi khoảng trắng, KHÔNG thêm giải thích, dấu câu hay số.\n" +
            "Ví dụ:\n" +
            "- Query: 'công nghệ AI' → công nghệ AI trí tuệ nhân tạo\n" +
            "- Query: 'video' → video phim clip mp4\n" +
            "- Query: 'haha' → haha vui nhộn hài hước",
            context, query
        );
    }
    
    private String callGeminiAPI(String prompt) throws IOException {
        JsonObject requestBody = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
        
        part.addProperty("text", prompt);
        parts.add(part);
        content.add("parts", parts);
        contents.add(content);
        requestBody.add("contents", contents);
        
        RequestBody body = RequestBody.create(
            gson.toJson(requestBody),
            MediaType.parse("application/json; charset=utf-8")
        );
        
        Request request = new Request.Builder()
            .url(this.apiUrl)
            .post(body)
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
            	if (response.code() == 429) {
            	    System.err.println("429 Response: " + response.body().string());
            	    throw new IOException("Rate limit exceeded: 429");
            	}
                throw new IOException("API call failed: " + response.code());
            }
            return response.body().string();
        }
    }
    
    private String parseResponse(String jsonResponse, String fallback) {
        try {
            JsonObject root = gson.fromJson(jsonResponse, JsonObject.class);
            String text = root.getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString()
                .trim()
                .replaceAll("[^\\p{L}\\p{N}\\s]", "")  // Loại bỏ dấu câu, số, ký tự đặc biệt
                .replaceAll("\\s+", " ");             // Chuẩn hóa khoảng trắng
            
            return text.isEmpty() ? fallback : text;
        } catch (Exception e) {
            return fallback;
        }
    }
}