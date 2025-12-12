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
    
    public String sendMessage(String message, Part imagePart, ArrayList<Map<String, Object>> chatHistory) 
            throws IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "gpt-4o-mini");
        requestBody.addProperty("max_tokens", 2000);
        
        JsonArray messages = new JsonArray();
        
        // System message
        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        systemMsg.addProperty("content", 
            "Bạn là trợ lý AI thông minh và hữu ích. Hãy trả lời ngắn gọn, rõ ràng bằng tiếng Việt.");
        messages.add(systemMsg);
        
        // Chat history
        if (chatHistory != null && !chatHistory.isEmpty()) {
            for (Map<String, Object> msg : chatHistory) {
                JsonObject historyMsg = new JsonObject();
                historyMsg.addProperty("role", (String) msg.get("role"));
                historyMsg.addProperty("content", (String) msg.get("content"));
                messages.add(historyMsg);
            }
        }
        
        // Current message
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        
        if (imagePart != null) {
            JsonArray content = new JsonArray();
            
            if (message != null && !message.trim().isEmpty()) {
                JsonObject textPart = new JsonObject();
                textPart.addProperty("type", "text");
                textPart.addProperty("text", message);
                content.add(textPart);
            }
            
            JsonObject imagePart2 = new JsonObject();
            imagePart2.addProperty("type", "image_url");
            JsonObject imageUrl = new JsonObject();
            imageUrl.addProperty("url", "data:image/jpeg;base64," + encodeImageToBase64(imagePart));
            imagePart2.add("image_url", imageUrl);
            content.add(imagePart2);
            
            userMsg.add("content", content);
        } else {
            userMsg.addProperty("content", message);
        }
        
        messages.add(userMsg);
        requestBody.add("messages", messages);
        
        RequestBody body = RequestBody.create(
            gson.toJson(requestBody),
            MediaType.parse("application/json; charset=utf-8")
        );
        
        Request request = new Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
            .post(body)
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("API call failed: " + response.code() + " - " + response.body().string());
            }
            
            JsonObject root = gson.fromJson(response.body().string(), JsonObject.class);
            return root.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();
        }
    }
    
    private String encodeImageToBase64(Part imagePart) throws IOException {
        try (InputStream is = imagePart.getInputStream()) {
            byte[] bytes = is.readAllBytes();
            return Base64.getEncoder().encodeToString(bytes);
        }
    }
}