package Modal.BaiVietEmbedding;

import java.util.ArrayList;

public class BaiVietEmbeddingBO {
    private BaiVietEmbeddingDAO dao = new BaiVietEmbeddingDAO();
    
    public void createDB(BaiVietEmbedding bve) throws Exception {
        dao.createDB(bve);
    }
    
    public ArrayList<BaiVietEmbedding> readDB() throws Exception {
        return dao.readDB();
    }
    
    public void deleteByMaBaiViet(long maBaiViet) throws Exception {
        dao.deleteByMaBaiViet(maBaiViet);
    }
    
    public BaiVietEmbedding findByMaBaiViet(long maBaiViet) throws Exception {
        return dao.findByMaBaiViet(maBaiViet);
    }
}