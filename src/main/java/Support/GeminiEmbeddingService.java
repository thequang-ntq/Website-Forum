package Support;

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
        
        String cleanedText = cleanText(text);
        
        int maxRetries = 5;
        int retryDelay = 1000; // 1 second
        
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
    
    private ArrayList<Double> createEmbeddingRequest(String cleanedText) throws IOException {
        JsonObject requestBody = new JsonObject();
        
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
        part.addProperty("text", cleanedText);
        parts.add(part);
        content.add("parts", parts);
        
        requestBody.add("content", content);
        
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
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                if (response.code() == 429) {
                    throw new IOException("429");
                }
                throw new IOException("API call failed: " + response.code() + " - " + errorBody);
            }
            
            String responseBody = response.body().string();
            JsonObject root = gson.fromJson(responseBody, JsonObject.class);
            
            JsonObject embedding = root.getAsJsonObject("embedding");
            JsonArray values = embedding.getAsJsonArray("values");
            
            ArrayList<Double> result = new ArrayList<>();
            for (int i = 0; i < values.size(); i++) {
                result.add(values.get(i).getAsDouble());
            }
            
            return result;
        }
    }
    
    // Tính cosine similarity
    public double cosineSimilarity(ArrayList<Double> vec1, ArrayList<Double> vec2) {
        if (vec1.size() != vec2.size()) {
            throw new IllegalArgumentException("Vectors must have same size");
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vec1.size(); i++) {
            dotProduct += vec1.get(i) * vec2.get(i);
            norm1 += vec1.get(i) * vec1.get(i);
            norm2 += vec2.get(i) * vec2.get(i);
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    public String embeddingToJson(ArrayList<Double> embedding) {
        return gson.toJson(embedding);
    }
    
    public ArrayList<Double> jsonToEmbedding(String json) {
        return gson.fromJson(json, ArrayList.class);
    }
    
    private String cleanText(String text) {
        String cleaned = text.replaceAll("<[^>]*>", "");
        cleaned = cleaned.replaceAll("[\\s]+", " ");
        if (cleaned.length() > 8000) {
            cleaned = cleaned.substring(0, 8000);
        }
        return cleaned.trim();
    }
}