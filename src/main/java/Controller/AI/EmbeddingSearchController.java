package Controller.AI;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import Modal.BaiViet.BaiViet;
import Modal.BaiViet.BaiVietBO;
import Modal.BaiVietEmbedding.BaiVietEmbedding;
import Modal.BaiVietEmbedding.BaiVietEmbeddingBO;
import Modal.BinhLuan.BinhLuan;
import Modal.BinhLuan.BinhLuanBO;
import Modal.BinhLuanEmbedding.BinhLuanEmbedding;
import Modal.BinhLuanEmbedding.BinhLuanEmbeddingBO;
import Support.GeminiEmbeddingService;

@WebServlet("/EmbeddingSearchController")
public class EmbeddingSearchController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private GeminiEmbeddingService embeddingService;
    private BaiVietBO bvbo;
    private BinhLuanBO blbo;
    private BaiVietEmbeddingBO bveBO;
    private BinhLuanEmbeddingBO bleBO;
    private Gson gson;
    
    public EmbeddingSearchController() {
        super();
        this.embeddingService = new GeminiEmbeddingService();
        this.bvbo = new BaiVietBO();
        this.blbo = new BinhLuanBO();
        this.bveBO = new BaiVietEmbeddingBO();
        this.bleBO = new BinhLuanEmbeddingBO();
        this.gson = new Gson();
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        
        String query = request.getParameter("query");
        String type = request.getParameter("type"); // "baiviet" or "binhluan"
        
        JsonObject result = new JsonObject();
        PrintWriter out = response.getWriter();
        
        try {
            if (query == null || query.trim().isEmpty()) {
                result.addProperty("success", false);
                result.addProperty("message", "Query không được để trống");
                out.print(result.toString());
                return;
            }
            
            // Tạo embedding cho query, vector của query là key được nhập vào trong search box
            ArrayList<Double> queryEmbedding = embeddingService.createEmbedding(query);
            
            // Theo bài viết hoặc bình luận, success là trạng thái thành công, matches là kết quả tìm kiếm
            if ("baiviet".equals(type)) {
                JsonArray matches = searchBaiViet(queryEmbedding);
                result.addProperty("success", true);
                result.add("matches", matches);
            } else if ("binhluan".equals(type)) {
                JsonArray matches = searchBinhLuan(queryEmbedding);
                result.addProperty("success", true);
                result.add("matches", matches);
            } else {
                result.addProperty("success", false);
                result.addProperty("message", "Type không hợp lệ");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            result.addProperty("success", false);
            result.addProperty("message", e.getMessage());
        }
        
        out.print(result.toString());
        out.flush();
    }
    
    // Tìm kiếm top 20 bài viết liên quan theo cosine similarity
    private JsonArray searchBaiViet(ArrayList<Double> queryEmbedding) throws Exception {
        ArrayList<BaiViet> dsBaiViet = bvbo.filterDB_trangThai("Active");
        ArrayList<BaiVietEmbedding> dsEmbeddings = bveBO.readDB();
        
        // Map để lưu điểm similarity
        Map<Long, Double> similarityMap = new HashMap<>();
        
        for (BaiVietEmbedding bve : dsEmbeddings) {
        	// Lấy ra embedding của bài viết
            ArrayList<Double> embedding = embeddingService.jsonToEmbedding(bve.getEmbedding());
            // So sánh với vector cần tìm kiếm
            double similarity = embeddingService.cosineSimilarity(queryEmbedding, embedding);
            // cho vào hashmap để so sánh sau
            similarityMap.put(bve.getMaBaiViet(), similarity);
        }
        
        // Sắp xếp theo similarity giảm dần
        dsBaiViet.sort((a, b) -> {
            Double simA = similarityMap.getOrDefault(a.getMaBaiViet(), 0.0);
            Double simB = similarityMap.getOrDefault(b.getMaBaiViet(), 0.0);
            return simB.compareTo(simA);
        });
        
        // Trả về top 20 kết quả với similarity > 0.5. Lưu mã bài viết và độ tương đồng.
        JsonArray matches = new JsonArray();
        int count = 0;
        for (BaiViet bv : dsBaiViet) {
            Double similarity = similarityMap.get(bv.getMaBaiViet());
            if (similarity != null && similarity > 0.5 && count < 20) {
                JsonObject match = new JsonObject();
                match.addProperty("maBaiViet", bv.getMaBaiViet());
                match.addProperty("similarity", similarity);
                matches.add(match);
                count++;
            }
        }
        
        // Trả về kết quả 20 bài viết giảm dần.
        return matches;
    }
    
    // Tương tự bài viết
    private JsonArray searchBinhLuan(ArrayList<Double> queryEmbedding) throws Exception {
        ArrayList<BinhLuan> dsBinhLuan = blbo.filterDB_trangThai("Active");
        ArrayList<BinhLuanEmbedding> dsEmbeddings = bleBO.readDB();
        
        Map<Long, Double> similarityMap = new HashMap<>();
        
        for (BinhLuanEmbedding ble : dsEmbeddings) {
            ArrayList<Double> embedding = embeddingService.jsonToEmbedding(ble.getEmbedding());
            double similarity = embeddingService.cosineSimilarity(queryEmbedding, embedding);
            similarityMap.put(ble.getMaBinhLuan(), similarity);
        }
        
        dsBinhLuan.sort((a, b) -> {
            Double simA = similarityMap.getOrDefault(a.getMaBinhLuan(), 0.0);
            Double simB = similarityMap.getOrDefault(b.getMaBinhLuan(), 0.0);
            return simB.compareTo(simA);
        });
        
        JsonArray matches = new JsonArray();
        int count = 0;
        for (BinhLuan bl : dsBinhLuan) {
            Double similarity = similarityMap.get(bl.getMaBinhLuan());
            if (similarity != null && similarity > 0.5 && count < 20) {
                JsonObject match = new JsonObject();
                match.addProperty("maBinhLuan", bl.getMaBinhLuan());
                match.addProperty("similarity", similarity);
                matches.add(match);
                count++;
            }
        }
        
        return matches;
    }
}