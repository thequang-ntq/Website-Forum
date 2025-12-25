package Modal.TinNhanChat;

import java.sql.*;
import java.util.ArrayList;
import Config.DBConfig;

public class TinNhanChatDAO {
    
	// Lấy tất cả tin nhắn trong đoạn chat dựa trên mã đoạn chat, theo thời điểm tạo xa nhất, tức là theo trục thời gian tạo tin nhắn
	// từ lúc bắt đầu tới lúc mới nhất. Tin nhắn trong đoạn chat.
    public ArrayList<TinNhanChat> readDB(long maDoanChat) throws Exception {
        ArrayList<TinNhanChat> ds = new ArrayList<>();
        String sql = "SELECT * FROM TinNhanChat WHERE MaDoanChat = ? ORDER BY ThoiDiemTao ASC";
        PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
        pr.setLong(1, maDoanChat);
        ResultSet rs = pr.executeQuery();
        
        while(rs.next()) {
            ds.add(new TinNhanChat(
                rs.getLong("MaTinNhan"),
                rs.getLong("MaDoanChat"),
                rs.getNString("Role"),
                rs.getNString("NoiDung"),
                rs.getNString("Url"),
                rs.getTimestamp("ThoiDiemTao")
            ));
        }
        pr.close();
        rs.close();
        return ds;
    }
    
    public ArrayList<TinNhanChat> readDB2() throws Exception {
        ArrayList<TinNhanChat> ds = new ArrayList<>();
        String sql = "SELECT * FROM TinNhanChat ORDER BY ThoiDiemTao ASC";
        PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
        ResultSet rs = pr.executeQuery();
        
        while(rs.next()) {
            ds.add(new TinNhanChat(
                rs.getLong("MaTinNhan"),
                rs.getLong("MaDoanChat"),
                rs.getNString("Role"),
                rs.getNString("NoiDung"),
                rs.getNString("Url"),
                rs.getTimestamp("ThoiDiemTao")
            ));
        }
        pr.close();
        rs.close();
        return ds;
    }
    
    //Lưu tin nhắn vào CSDL, vào của đoạn chat tương ứng
    public void createDB(TinNhanChat tnc) throws Exception {
        String sql = "INSERT INTO TinNhanChat (MaDoanChat, Role, NoiDung, Url) VALUES (?, ?, ?, ?)";
        PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
        pr.setLong(1, tnc.getMaDoanChat());
        pr.setNString(2, tnc.getRole());
        pr.setNString(3, tnc.getNoiDung());
        pr.setNString(4, tnc.getUrl());
        pr.executeUpdate();
        pr.close();
    }
}