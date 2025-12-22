package Support;

import java.util.ArrayList;

import Modal.BaiViet.BaiViet;
import Modal.BaiViet.BaiVietBO;
import Modal.BaiVietEmbedding.BaiVietEmbedding;
import Modal.BaiVietEmbedding.BaiVietEmbeddingBO;
import Modal.BinhLuan.BinhLuan;
import Modal.BinhLuan.BinhLuanBO;
import Modal.BinhLuanEmbedding.BinhLuanEmbedding;
import Modal.BinhLuanEmbedding.BinhLuanEmbeddingBO;

public class GenerateExistingEmbeddings {
    
    public static void main(String[] args) {
        GeminiEmbeddingService embeddingService = new GeminiEmbeddingService();
        BaiVietBO bvbo = new BaiVietBO();
        BaiVietEmbeddingBO bveBO = new BaiVietEmbeddingBO();
        BinhLuanBO blbo = new BinhLuanBO();
        BinhLuanEmbeddingBO bleBO = new BinhLuanEmbeddingBO();
        
        try {
            // Generate embeddings cho bài viết
            ArrayList<BaiViet> dsBaiViet = bvbo.readDB();
            System.out.println("Đang tạo embedding cho " + dsBaiViet.size() + " bài viết...");
            
            int successCount = 0;
            int skipCount = 0;
            int errorCount = 0;
            
            for (int i = 0; i < dsBaiViet.size(); i++) {
                BaiViet bv = dsBaiViet.get(i);
                try {
                    // Kiểm tra xem đã có embedding chưa
                    BaiVietEmbedding existing = bveBO.findByMaBaiViet(bv.getMaBaiViet());
                    if (existing != null) {
                        System.out.println("Bài viết " + bv.getMaBaiViet() + " đã có embedding, bỏ qua");
                        skipCount++;
                        continue;
                    }
                    
                    String content = bv.getTieuDe() + " " + bv.getNoiDung();
                    ArrayList<Double> embedding = embeddingService.createEmbedding(content);
                    String embeddingJson = embeddingService.embeddingToJson(embedding);
                    
                    BaiVietEmbedding bve = new BaiVietEmbedding();
                    bve.setMaBaiViet(bv.getMaBaiViet());
                    bve.setEmbedding(embeddingJson);
                    bveBO.createDB(bve);
                    
                    successCount++;
                    System.out.println("✓ Đã tạo embedding cho bài viết " + (i+1) + "/" + dsBaiViet.size() + " (ID: " + bv.getMaBaiViet() + ")");
                    
                    // Delay để tránh rate limit
                    Thread.sleep(1000); // 1 second
                    
                } catch (Exception e) {
                    errorCount++;
                    System.err.println("✗ Lỗi tạo embedding cho bài viết " + bv.getMaBaiViet() + ": " + e.getMessage());
                }
            }
            
            System.out.println("\n=== Tổng kết Bài viết ===");
            System.out.println("Thành công: " + successCount);
            System.out.println("Đã có sẵn: " + skipCount);
            System.out.println("Lỗi: " + errorCount);
            
            // Generate embeddings cho bình luận
            ArrayList<BinhLuan> dsBinhLuan = blbo.readDB();
            System.out.println("\nĐang tạo embedding cho " + dsBinhLuan.size() + " bình luận...");
            
            successCount = 0;
            skipCount = 0;
            errorCount = 0;
            
            for (int i = 0; i < dsBinhLuan.size(); i++) {
                BinhLuan bl = dsBinhLuan.get(i);
                try {
                    BinhLuanEmbedding existing = bleBO.findByMaBinhLuan(bl.getMaBinhLuan());
                    if (existing != null) {
                        System.out.println("Bình luận " + bl.getMaBinhLuan() + " đã có embedding, bỏ qua");
                        skipCount++;
                        continue;
                    }
                    
                    ArrayList<Double> embedding = embeddingService.createEmbedding(bl.getNoiDung());
                    String embeddingJson = embeddingService.embeddingToJson(embedding);
                    
                    BinhLuanEmbedding ble = new BinhLuanEmbedding();
                    ble.setMaBinhLuan(bl.getMaBinhLuan());
                    ble.setEmbedding(embeddingJson);
                    bleBO.createDB(ble);
                    
                    successCount++;
                    System.out.println("✓ Đã tạo embedding cho bình luận " + (i+1) + "/" + dsBinhLuan.size() + " (ID: " + bl.getMaBinhLuan() + ")");
                    
                    Thread.sleep(1000);
                    
                } catch (Exception e) {
                    errorCount++;
                    System.err.println("✗ Lỗi tạo embedding cho bình luận " + bl.getMaBinhLuan() + ": " + e.getMessage());
                }
            }
            
            System.out.println("\n=== Tổng kết Bình luận ===");
            System.out.println("Thành công: " + successCount);
            System.out.println("Đã có sẵn: " + skipCount);
            System.out.println("Lỗi: " + errorCount);
            
            System.out.println("\n✓ Hoàn thành!");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}