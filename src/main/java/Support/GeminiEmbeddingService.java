package Support;
// Sử dụng Geminu Api Key để embedding nội dung bài viết và bình luận thành vector
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import Modal.ApiKey.ApiKeyBO;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;

public class GeminiEmbeddingService {
    private static ApiKeyBO apikeybo;
    private static String API_KEY;
    private static final String API_URL_TEMPLATE = "https://generativelanguage.googleapis.com/v1beta/models/text-embedding-004:embedContent?key=%s";
    
    private final OkHttpClient client;
    private final Gson gson;
    private final String apiUrl;
    
    public GeminiEmbeddingService() {
        this.client = OkHttpClientManager.getClient();
        this.gson = new Gson();
        this.apiUrl = buildApiUrl();
    }
    
    private static synchronized String getApiKey() {
        if (API_KEY == null) {
            try {
                apikeybo = new ApiKeyBO();
                API_KEY = apikeybo.getApiKey(); // Sử dụng Gemini API key đã có
                if (API_KEY == null || API_KEY.trim().isEmpty()) {
                    throw new IllegalStateException("Gemini API Key không được cấu hình!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return API_KEY;
    }
    
    private String buildApiUrl() {
        return String.format(API_URL_TEMPLATE, getApiKey());
    }
    
    // Tạo embedding từ text với retry mechanism
    public ArrayList<Double> createEmbedding(String text) throws IOException {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text không được rỗng");
        }
        
        // Làm sạch text
        String cleanedText = cleanText(text);
        
        // Tối đa thử 5 lần
        int maxRetries = 5;
        // Khoảng thời gian giữa các lần thử
        int retryDelay = 1000; // 1 second
        
        // Retry xử lý embedding
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                return createEmbeddingRequest(cleanedText);
            } catch (IOException e) {
                if (e.getMessage().contains("429") || e.getMessage().contains("RESOURCE_EXHAUSTED")) {
                    if (attempt < maxRetries - 1) {
                        System.out.println("Rate limit hit, retrying in " + (retryDelay/1000) + " seconds... (Attempt " + (attempt+1) + "/" + maxRetries + ")");
                        try {
                            Thread.sleep(retryDelay);
                            retryDelay = Math.min(retryDelay * 2, 10000); // Max 10s
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new IOException("Interrupted during retry", ie);
                        }
                    } else {
                        throw new IOException("Rate limit exceeded after " + maxRetries + " attempts. Please try again later.");
                    }
                } else {
                    throw e;
                }
            }
        }
        
        throw new IOException("Failed to create embedding after " + maxRetries + " attempts");
    }
    
    // Tạo embedding, vector chứa các giá trị là vector embedding cho nội dung bài viết, bình luận.
    // Gửi Nội dung text lên API Embedding, sau đó đọc vector embedding và trả về mảng số thực
    private ArrayList<Double> createEmbeddingRequest(String cleanedText) throws IOException {
    	// Object gốc
        JsonObject requestBody = new JsonObject();
        
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();
        // part chứa text. API yêu cầu text nằm trong parts[].text
        JsonObject part = new JsonObject();
        part.addProperty("text", cleanedText);
        // Gói part vào parts[]
        parts.add(part);
        // Gói parts vào content
        content.add("parts", parts);
        // Cấu trúc: {
//        "content": {
//            "parts": [
//              { "text": "Nội dung đã clean" }
//            ]
//          }
//        }
        // Gắn vào requestBody
        requestBody.add("content", content);
        
        // Tạo HTTP Body OkHttp convert JSON -> String
        RequestBody body = RequestBody.create(
            gson.toJson(requestBody),
            MediaType.parse("application/json; charset=utf-8")
        );
        
        // Tạo Http Request: apiUrl là endpoint tạo embedding, body là json ở trên, method là post
        Request request = new Request.Builder()
            .url(this.apiUrl)
            .post(body)
            .build();
        
        // Gửi request & nhận response: request đồng bộ, auto close response 
        try (Response response = client.newCall(request).execute()) {
        	// Kiểm tra lỗi HTTP
            if (!response.isSuccessful()) {
            	// Lấy lỗi nếu có
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                // error 429 là rate limit
                if (response.code() == 429) {
                    throw new IOException("429");
                }
                // các lỗi khác
                throw new IOException("API call failed: " + response.code() + " - " + errorBody);
            }
            
            // parse response object, convert json response -> object
            String responseBody = response.body().string();
            JsonObject root = gson.fromJson(responseBody, JsonObject.class);
            
            // Lấy object embedding	
            JsonObject embedding = root.getAsJsonObject("embedding");
            // Lấy mảng vector
            JsonArray values = embedding.getAsJsonArray("values");
            // Response dạng:
//            {
//            	  "embedding": {
//            	    "values": [0.0123, -0.4567, ...]
//            	  }
//            	}
            
            // convert json -> mảng số thực
            ArrayList<Double> result = new ArrayList<>();
            for (int i = 0; i < values.size(); i++) {
                result.add(values.get(i).getAsDouble());
            }
            
            return result;
        }
    }
    
    // Tính cosine similarity
    // Hàm đo độ giống nhau ngữ nghĩa liên quan đến NLP - Natural Language Processing
    // Đo độ tương đồng ngữ nghĩa giữa 2 bài viết / 2 bình luận
    // Trả về giá trị từ -1 đến 1, với embedding thường là từ 0 đến 1.
    // 1 là liên quan cao nhất, 0 là hoàn toàn không liên quan.
    public double cosineSimilarity(ArrayList<Double> vec1, ArrayList<Double> vec2) {
    	// Bắt buộc cùng model embedding, cùng số chiều
        if (vec1.size() != vec2.size()) {
            throw new IllegalArgumentException("Vectors must have same size");
        }
        
        // Tính toán cosine similarity, công thức: cosine = (A.B) / (|A| * |B|)
        double dotProduct = 0.0; // Tích vô hướng
        double norm1 = 0.0; // Độ dài vector 1
        double norm2 = 0.0; // Độ dài vector 2
        for (int i = 0; i < vec1.size(); i++) {
            dotProduct += vec1.get(i) * vec2.get(i);
            norm1 += vec1.get(i) * vec1.get(i);
            norm2 += vec2.get(i) * vec2.get(i);
        }
        // kết quả theo công thức trên
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    // Chuyển vector embedding -> Chuỗi JSON. Lưu vào cột embedding trong bảng CSDL. Lưu vào CSDL
    public String embeddingToJson(ArrayList<Double> embedding) {
        return gson.toJson(embedding);
    }
    
    // Đọc embedding từ CSDL. Convert JSON -> mảng số thực.
    public ArrayList<Double> jsonToEmbedding(String json) {
        return gson.fromJson(json, ArrayList.class);
    }
    
    // Loại bỏ thẻ HTML, chuẩn hóa khoảng trắng, giới hạn độ dài 8000 ký tự, cắt khoảng trắng thừa đầu - cuối.
    private String cleanText(String text) {
    	// Loại bỏ thẻ HTML
        String cleaned = text.replaceAll("<[^>]*>", "");
        //Gộp nhiều space -> 1 space, gộp \n - xuống dòng, \t - tab, \r - đưa trỏ về đầu dòng -> space
        cleaned = cleaned.replaceAll("[\\s]+", " ");
        if (cleaned.length() > 8000) {
            cleaned = cleaned.substring(0, 8000);
        }
        return cleaned.trim();
    }
}