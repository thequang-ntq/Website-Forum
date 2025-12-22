package Modal.BinhLuanEmbedding;

import java.util.ArrayList;

public class BinhLuanEmbeddingBO {
    private BinhLuanEmbeddingDAO dao = new BinhLuanEmbeddingDAO();
    
    public void createDB(BinhLuanEmbedding bve) throws Exception {
        dao.createDB(bve);
    }
    
    public ArrayList<BinhLuanEmbedding> readDB() throws Exception {
        return dao.readDB();
    }
    
    public void deleteByMaBinhLuan(long maBinhLuan) throws Exception {
        dao.deleteByMaBinhLuan(maBinhLuan);
    }
    
    public BinhLuanEmbedding findByMaBinhLuan(long maBinhLuan) throws Exception {
        return dao.findByMaBinhLuan(maBinhLuan);
    }
}