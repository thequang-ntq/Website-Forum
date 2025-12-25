package Modal.BinhLuanEmbedding;

import java.util.ArrayList;

public class BinhLuanEmbeddingBO {
    private BinhLuanEmbeddingDAO dao = new BinhLuanEmbeddingDAO();
    ArrayList<BinhLuanEmbedding> ds;
   
    public ArrayList<BinhLuanEmbedding> readDB() throws Exception {
        ds = dao.readDB();
        return ds;
    }
    
    public void createDB(long MaBinhLuan, String Embedding) throws Exception {
        if (MaBinhLuan < 1) {
            throw new Exception("Mã bình luận không được để trống!");
        }
        if (Embedding == null || Embedding.trim().isEmpty()) {
            throw new Exception("Nội dung vector embedding không được để trống!");
        }
        
        BinhLuanEmbedding ble = new BinhLuanEmbedding(-1, MaBinhLuan, Embedding, null);
        dao.createDB(ble);
    }
    
    // Sửa nội dung bình luận thì cho: delete rồi create lại.
    
    public void deleteDB(long MaBinhLuan) throws Exception {
        if (MaBinhLuan < 1) {
            throw new Exception("Mã bình luận không được để trống!");
        }
        if (dao.findByMaBinhLuan(MaBinhLuan) == null) {
            throw new Exception("Bình luận không tồn tại!");
        }
        dao.deleteDB(MaBinhLuan);
    }
    
    public BinhLuanEmbedding findByMaBinhLuan(long maBinhLuan) throws Exception {
        return dao.findByMaBinhLuan(maBinhLuan);
    }
}