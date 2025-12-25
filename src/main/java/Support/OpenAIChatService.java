package Support;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

import javax.servlet.http.Part;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import Modal.ApiKey.ApiKeyBO;
import okhttp3.*;

public class OpenAIChatService {
    private static String OPENAI_API_KEY;
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    
    private final OkHttpClient client;
    private final Gson gson;
    
    public OpenAIChatService() {
        this.client = OkHttpClientManager.getClient();
        this.gson = new Gson();
        loadApiKey();
    }
    
    private static synchronized void loadApiKey() {
        if (OPENAI_API_KEY == null) {
            try {
                ApiKeyBO apikeybo = new ApiKeyBO();
                OPENAI_API_KEY = apikeybo.getOpenAIKey();
                if (OPENAI_API_KEY == null || OPENAI_API_KEY.trim().isEmpty()) {
                    throw new IllegalStateException("OpenAI API Key không được cấu hình!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    // Gửi tin nhắn text có thể kèm ảnh, có lịch sử chat, gọi OpenAI Chat Completion API, nhận câu trả lời từ AI và trả về dưới dạng String
    public String sendMessage(String message, Part imagePart, ArrayList<Map<String, Object>> chatHistory) 
            throws IOException {
    	// Tạo requestbody
        JsonObject requestBody = new JsonObject();
        // Chọn model AI
        requestBody.addProperty("model", "gpt-4o-mini");
        // Số token tối đa AI được phép trả lời.
        requestBody.addProperty("max_tokens", 2000);
        
        // Tạo mảng messages
        JsonArray messages = new JsonArray();
        // Cấu trúc
//        messages: [
//	       { "role": "system", "content": "..." },
//	       { "role": "user", "content": "..." },
//	       { "role": "assistant", "content": "..." }
//	     ]

        // System message - Định nghĩa vai trò AI
        // Trả lời tiếng Việt, ngắn gọn, rõ ràng
        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        systemMsg.addProperty("content", 
            "Bạn là trợ lý AI thông minh và hữu ích. Hãy trả lời ngắn gọn, rõ ràng bằng tiếng Việt.");
        messages.add(systemMsg);
        
        // Chat history - lịch sử Chat, giữ ngữ cảnh hội thoại, AI nhớ các câu hỏi / trả lời trước đó.
        if (chatHistory != null && !chatHistory.isEmpty()) {
            for (Map<String, Object> msg : chatHistory) {
                JsonObject historyMsg = new JsonObject();
                historyMsg.addProperty("role", (String) msg.get("role"));
                historyMsg.addProperty("content", (String) msg.get("content"));
                messages.add(historyMsg);
            }
        }
        //VD
//        { role: "user", content: "Xin chào" }
//        { role: "assistant", content: "Chào bạn!" }

        // Current message - tin nhắn hiện tại
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        
        // Trường hợp có ảnh -> content không còn là String mà là mảng đa phương thức.
        if (imagePart != null) {
            JsonArray content = new JsonArray();
            // Thêm text (nếu có)
            // VD:
//            { "type": "text", "text": "Ảnh này nói về gì?" }
            if (message != null && !message.trim().isEmpty()) {
                JsonObject textPart = new JsonObject();
                textPart.addProperty("type", "text");
                textPart.addProperty("text", message);
                content.add(textPart);
            }
            
            // Thêm ảnh (base 64)
            JsonObject imagePart2 = new JsonObject();
            imagePart2.addProperty("type", "image_url");
            JsonObject imageUrl = new JsonObject();
            imageUrl.addProperty("url", "data:image/jpeg;base64," + encodeImageToBase64(imagePart));
            imagePart2.add("image_url", imageUrl);
            content.add(imagePart2);
            // VD:
//            {
//        	  "type": "image_url",
//        	  "image_url": {
//        	    "url": "data:image/jpeg;base64,...."
//        	  }
//        	}
            // Gán content cho userMsg
            userMsg.add("content", content);
        } else {
        	// TH không ảnh, chỉ gửi text bth
            userMsg.addProperty("content", message);
        }
        
        // Đưa userMsg vào messages.
        messages.add(userMsg);
        requestBody.add("messages", messages);
        
        // Gửi request bằng OkHttp
        RequestBody body = RequestBody.create(
            gson.toJson(requestBody),
            MediaType.parse("application/json; charset=utf-8")
        );
        
        // Post Request tới OpenAI API
        Request request = new Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
            .post(body)
            .build();
        
        // Nhận và xử lý response
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("API call failed: " + response.code() + " - " + response.body().string());
            }
            
            // parse JSON trả về.
            // VD
//            {
//        	  "choices": [
//        	    {
//        	      "message": {
//        	        "role": "assistant",
//        	        "content": "Đây là hình ảnh..."
//        	      }
//        	    }
//        	  ]
//        	}
            JsonObject root = gson.fromJson(response.body().string(), JsonObject.class);
            // Trả về nội dung AI trả lồi là content
            return root.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();
        }
    }
    
    //encode ảnh sang base64, đọc ảnh Upload, chuyển sang base64, gửi được qua JSON
    private String encodeImageToBase64(Part imagePart) throws IOException {
        try (InputStream is = imagePart.getInputStream()) {
            byte[] bytes = is.readAllBytes();
            return Base64.getEncoder().encodeToString(bytes);
        }
    }
}