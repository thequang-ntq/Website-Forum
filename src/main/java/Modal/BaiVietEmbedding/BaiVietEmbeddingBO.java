package Modal.BaiVietEmbedding;

import java.util.ArrayList;

public class BaiVietEmbeddingBO {
    private BaiVietEmbeddingDAO dao = new BaiVietEmbeddingDAO();
    ArrayList<BaiVietEmbedding> ds;
    
    public ArrayList<BaiVietEmbedding> readDB() throws Exception {
        ds = dao.readDB();
        return ds;
    }
    
    public void createDB(long MaBaiViet, String Embedding) throws Exception {
        if (MaBaiViet < 1) {
            throw new Exception("Mã bài viết không được để trống!");
        }
        if (Embedding == null || Embedding.trim().isEmpty()) {
            throw new Exception("Nội dung vector embedding không được để trống!");
        }
        
        BaiVietEmbedding bve = new BaiVietEmbedding(-1, MaBaiViet, Embedding, null);
        dao.createDB(bve);
    }
    
    // Sửa nội dung bài viết thì: cho delete rồi create lại.
    
    public void deleteDB(long MaBaiViet) throws Exception {
        if (MaBaiViet < 1) {
            throw new Exception("Mã bài viết không được để trống!");
        }
        if (dao.findByMaBaiViet(MaBaiViet) == null) {
            throw new Exception("Bài viết không tồn tại!");
        }
        
        dao.deleteDB(MaBaiViet);
    }
    
    public BaiVietEmbedding findByMaBaiViet(long maBaiViet) throws Exception {
        return dao.findByMaBaiViet(maBaiViet);
    }
}