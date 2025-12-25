package Modal.BinhLuanEmbedding;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import Config.DBConfig;

public class BinhLuanEmbeddingDAO {
    public ArrayList<BinhLuanEmbedding> readDB() throws Exception {
        ArrayList<BinhLuanEmbedding> ds = new ArrayList<>();
        String sql = "SELECT * FROM BinhLuanEmbedding;";
        PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
        ResultSet rs = pr.executeQuery();
        while (rs.next()) {
            long ID = rs.getLong("ID");
            long MaBinhLuan = rs.getLong("MaBinhLuan");
            String Embedding = rs.getNString("Embedding");
            Timestamp ThoiDiemTao = rs.getTimestamp("ThoiDiemTao");
            ds.add(new BinhLuanEmbedding(ID, MaBinhLuan, Embedding, ThoiDiemTao));
        }
        pr.close();
        rs.close();
        return ds;
    }
    
    public void createDB(BinhLuanEmbedding ble) throws Exception {
        String sql = "INSERT INTO BinhLuanEmbedding (MaBinhLuan, Embedding) VALUES (?, ?);";
        PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
        pr.setLong(1, ble.getMaBinhLuan());
        pr.setNString(2, ble.getEmbedding());
        pr.executeUpdate();
        pr.close();
    }
    
    public void deleteDB(long maBinhLuan) throws Exception {
        String sql = "DELETE FROM BinhLuanEmbedding WHERE MaBinhLuan = ?;";
        PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
        pr.setLong(1, maBinhLuan);
        pr.executeUpdate();
        pr.close();
    }
    
    public BinhLuanEmbedding findByMaBinhLuan(long maBinhLuan) throws Exception {
        String sql = "SELECT * FROM BinhLuanEmbedding WHERE MaBinhLuan = ?;";
        PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
        pr.setLong(1, maBinhLuan);
        ResultSet rs = pr.executeQuery();
        BinhLuanEmbedding ble = null;
        if (rs.next()) {
            long ID = rs.getLong("ID");
            long MaBinhLuan = rs.getLong("MaBinhLuan");
            String Embedding = rs.getNString("Embedding");
            Timestamp ThoiDiemTao = rs.getTimestamp("ThoiDiemTao");
            ble = new BinhLuanEmbedding(ID, MaBinhLuan, Embedding, ThoiDiemTao);
        }
        pr.close();
        rs.close();
        return ble;
    }
}