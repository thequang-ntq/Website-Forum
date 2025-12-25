package Modal.BaiVietEmbedding;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import Config.DBConfig;

public class BaiVietEmbeddingDAO {
    public ArrayList<BaiVietEmbedding> readDB() throws Exception {
        ArrayList<BaiVietEmbedding> ds = new ArrayList<>();
        String sql = "SELECT * FROM BaiVietEmbedding;";
        PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
        ResultSet rs = pr.executeQuery();
        while (rs.next()) {
            long ID = rs.getLong("ID");
            long MaBaiViet = rs.getLong("MaBaiViet");
            String Embedding = rs.getNString("Embedding");
            Timestamp ThoiDiemTao = rs.getTimestamp("ThoiDiemTao");
            ds.add(new BaiVietEmbedding(ID, MaBaiViet, Embedding, ThoiDiemTao));
        }
        pr.close();
        rs.close();
        return ds;
    }
    
    public void createDB(BaiVietEmbedding bve) throws Exception {
        String sql = "INSERT INTO BaiVietEmbedding (MaBaiViet, Embedding) VALUES (?, ?);";
        PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
        pr.setLong(1, bve.getMaBaiViet());
        pr.setNString(2, bve.getEmbedding());
        pr.executeUpdate();
        pr.close();
    }
    
    public void deleteDB(long maBaiViet) throws Exception {
        String sql = "DELETE FROM BaiVietEmbedding WHERE MaBaiViet = ?;";
        PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
        pr.setLong(1, maBaiViet);
        pr.executeUpdate();
        pr.close();
    }
    
    public BaiVietEmbedding findByMaBaiViet(long maBaiViet) throws Exception {
        String sql = "SELECT * FROM BaiVietEmbedding WHERE MaBaiViet = ?;";
        PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
        pr.setLong(1, maBaiViet);
        ResultSet rs = pr.executeQuery();
        BaiVietEmbedding bve = null;
        if (rs.next()) {
            long ID = rs.getLong("ID");
            long MaBaiViet = rs.getLong("MaBaiViet");
            String Embedding = rs.getNString("Embedding");
            Timestamp ThoiDiemTao = rs.getTimestamp("ThoiDiemTao");
            bve = new BaiVietEmbedding(ID, MaBaiViet, Embedding, ThoiDiemTao);
        }
        pr.close();
        rs.close();
        return bve;
    }
}